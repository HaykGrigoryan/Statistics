package com.constantlab.statistics.ui.street;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.constantlab.statistics.R;
import com.constantlab.statistics.app.RealmManager;
import com.constantlab.statistics.models.Address;
import com.constantlab.statistics.models.Apartment;
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.BuildingStatus;
import com.constantlab.statistics.models.BuildingType;
import com.constantlab.statistics.models.History;
import com.constantlab.statistics.models.Street;
import com.constantlab.statistics.models.StreetType;
import com.constantlab.statistics.models.TempNewData;
import com.constantlab.statistics.ui.apartments.ApartmentDetailsFragment;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.utils.ConstKeys;
import com.constantlab.statistics.utils.GsonUtils;
import com.constantlab.statistics.utils.HistoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by Hayk on 04/01/2018.
 */

public class StreetDetailsFragment extends BaseFragment {
    Integer streetId;
    String streetName;
    Integer taskId;
    @BindView(R.id.et_street)
    EditText etStreetName;
    @BindView(R.id.layout_origin_name)
    TextInputLayout lOriginName;
    @BindView(R.id.layout_name)
    TextInputLayout lName;
    @BindView(R.id.et_street_origin_name)
    EditText etStreetOriginName;


    @BindView(R.id.sp_street_type)
    Spinner spStreetType;
    @BindView(R.id.title)
    TextView mTitle;
    private List<StreetType> mStreetType;
    private Street mStreet;

    public static StreetDetailsFragment newInstance(Integer streetId, String streetName, Integer taskId) {
        StreetDetailsFragment fragment = new StreetDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ConstKeys.TAG_STREET, streetId);
        args.putInt(ConstKeys.TAG_TASK, taskId);
        args.putString(ConstKeys.TAG_STREET_NAME, streetName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStreetType = new ArrayList<>();
        if (getArguments() != null) {
            streetId = getArguments().getInt(ConstKeys.TAG_STREET);
            streetName = getArguments().getString(ConstKeys.TAG_STREET_NAME);
            taskId = getArguments().getInt(ConstKeys.TAG_TASK);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_street_details, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (streetName != null) {
            mTitle.setText(streetName);
        }
        loadStreetType();
        showStoredInformation();
    }

    private void showStoredInformation() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            Street street = realm.where(Street.class).equalTo("id", streetId).equalTo("task_id", taskId).findFirst();
            if (street != null) {
                mStreet = realm.copyFromRealm(street);
                lName.setVisibility(View.VISIBLE);
                etStreetName.setEnabled(true);
                etStreetOriginName.setEnabled(false);
                showData(mStreet);
            } else {
                etStreetOriginName.setEnabled(true);
                etStreetName.setEnabled(false);
                lName.setVisibility(View.GONE);
            }

        } finally {
            if (realm != null)
                realm.close();
        }
    }

    private void showData(Street street) {
        etStreetOriginName.setText(street.getOriginalName());
        etStreetName.setText(street.getName());
        spStreetType.setSelection(StreetType.getIndex(mStreetType, street.getStreetTypeCode()));
    }

    @OnClick(R.id.iv_back)
    public void back() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @OnClick(R.id.btn_save)
    public void save() {
        boolean proceed = true;
        String name = "";
        if (mStreet != null) {
            etStreetName.setError(null);
            if (etStreetName.getText().toString().isEmpty()) {
                proceed = false;
                etStreetName.setError(getString(R.string.error_empty_field));
            } else {
                name = etStreetName.getText().toString();
            }
        } else {
            etStreetOriginName.setError(null);
            if (etStreetOriginName.getText().toString().isEmpty()) {
                proceed = false;
                etStreetOriginName.setError(getString(R.string.error_empty_field));
            } else {
                name = etStreetOriginName.getText().toString();
            }
        }


        if (proceed) {
            if (RealmManager.getInstance().checkStreetStreetName(taskId, streetId, name)) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.dialog_title_attention));
                builder.setMessage(getString(R.string.dialog_duplicate_street_name));
                builder.setPositiveButton(getString(R.string.label_ok), null);
                builder.show();
            } else {
                saveDataToDatabase();
            }

        }
    }

    private void saveDataToDatabase() {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(realmObject -> {
                Street s = realmObject.where(Street.class).equalTo("id", streetId).findFirst();
                Street street = null;
                boolean isNew = false;
                if (s != null) {
                    street = realmObject.copyFromRealm(s);
                    street.setNew(false);
                } else {
                    street = new Street();
                    Number currentIdNum = realmObject.where(Street.class).max("id");
                    int nextId;
                    if (currentIdNum == null) {
                        nextId = 1;
                    } else {
                        nextId = currentIdNum.intValue() + 1;
                    }
                    street.setId(nextId);

                    Number currentLocalIdNum = realmObject.where(Street.class).max("local_id");
                    int nextLocalId;
                    if (currentLocalIdNum == null) {
                        nextLocalId = 1;
                    } else {
                        nextLocalId = currentLocalIdNum.intValue() + 1;
                    }
                    street.setLocalId(nextLocalId);
                    street.setTaskId(taskId);
                    isNew = true;
                    street.setNew(true);
                }
                if (isNew) {
                    street.setOriginalName(etStreetOriginName.getText().toString());
                    street.setName(etStreetOriginName.getText().toString());
                } else {
                    street.setName(etStreetName.getText().toString().trim());
                }
                StreetType streetType = (StreetType) spStreetType.getSelectedItem();
                street.setStreetTypeCode(streetType.getId());

                if (!street.isNew()) {
                    if (!mStreet.getName().equals(street.getName())) {
                        addHistory(taskId, 4, new TempNewData(street.getName()), street.getId(), street.getId(), realmObject);
                    }

                    if (!mStreet.getStreetTypeCode().equals(street.getStreetTypeCode())) {
                        addHistory(taskId, 5, new TempNewData(String.valueOf(street.getStreetTypeCode())), street.getId(), street.getId(), realmObject);
                    }
                } else {
                    Integer referenceId = addHistory(taskId, 1, new TempNewData(street.getName()), taskId, street.getId(), realmObject);
                    street.setHistoryId(referenceId);
                    addHistory(taskId, 5, new TempNewData(String.valueOf(street.getStreetTypeCode())), null, street.getId(), realmObject, referenceId);
                }

                realmObject.insertOrUpdate(street);

            });
        } finally {
            if (realm != null)
                realm.close();
        }
        getActivity().onBackPressed();
    }

    private Integer addHistory(int taskId, int changeType, TempNewData newData, Integer streetId, Integer tempStreetId, Realm realm) {
        return addHistory(taskId, changeType, newData, streetId, tempStreetId, realm, null);
    }

    private Integer addHistory(int taskId, int changeType, TempNewData newData, Integer streetId, Integer tempStreetId, Realm realm, Integer referenceId) {
        History history = new History();
        history.setTaskId(taskId);
        history.setChangeType(changeType);
        history.setNewData(newData);
        history.setObjectId(streetId);
        history.setTempObjectId(tempStreetId);
        history.setReferenceId(referenceId);
        history.setObjectType(1);

        return HistoryManager.getInstance().addOrUpdateHistory(history, realm);
    }


    private void loadStreetType() {
        mStreetType = (List<StreetType>) RealmManager.getInstance().getTypes(StreetType.class);//GsonUtils.getStreetTypeData(getContext());

        ArrayAdapter<StreetType> arrayAdapterEditSt = new ArrayAdapter<StreetType>(getContext(), R.layout.spinner_item, mStreetType);
        arrayAdapterEditSt.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spStreetType.setAdapter(arrayAdapterEditSt);
    }
}
