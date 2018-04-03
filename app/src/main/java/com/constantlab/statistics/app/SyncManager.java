package com.constantlab.statistics.app;

import android.content.Context;

import com.constantlab.statistics.models.Apartment;
import com.constantlab.statistics.models.Building;
import com.constantlab.statistics.models.History;
import com.constantlab.statistics.models.Street;
import com.constantlab.statistics.models.Task;
import com.constantlab.statistics.models.send.ApartmentSend;
import com.constantlab.statistics.models.send.BuildingSend;
import com.constantlab.statistics.models.send.ChangeSend;
import com.constantlab.statistics.models.send.StreetSend;
import com.constantlab.statistics.models.send.TaskSend;
import com.constantlab.statistics.utils.DateUtils;
import com.constantlab.statistics.utils.SharedPreferencesManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by Hayk on 19/02/2018.
 */

public class SyncManager {
    public static List<TaskSend> construct(Context context, List<History> histories, Integer userId) {
        List<TaskSend> result = new ArrayList<>();

//        Map<Integer, List<History>> map = splitToTaskGroups(histories);
        for (History history : histories) {
            addObjectIfNeeded(result, history, context, userId);
        }
        cleanUpBeforeSend(result);
//        Type listType = new TypeToken<List<TaskSend>>() {
//        }.getType();
//        String json = new Gson().toJson(result, listType);
        return result;
    }

    private static void addTaskIfNeeded(List<TaskSend> taskSendList, Integer taskId, Context context) {
        boolean isExists = false;
        for (TaskSend taskSend : taskSendList) {
            if (taskSend.getTaskId().equals(taskId)) {
                isExists = true;
            }
        }
        if (!isExists) {
            taskSendList.add(new TaskSend(taskId, SharedPreferencesManager.getInstance().getUser(context).getKey()));
        }
    }

    private static void addStreetIfNeeded(List<TaskSend> taskSendList, Integer taskId, Street street) {
        boolean isExists = false;
        TaskSend task = null;
        for (TaskSend taskSend : taskSendList) {
            if (taskSend.getTaskId().equals(taskId)) {
                if (!isExists) {
                    task = taskSend;
                }
                for (StreetSend streetSend : taskSend.getStreets()) {
                    if (streetSend.getStreetId().equals(street.getId())) {
                        isExists = true;
                    }
                }

            }
        }

        if (!isExists) {
            StreetSend streetSend = new StreetSend(street.getId(), street.isNew() ? 1 : 0);
            if (street.getChangeTime() > 0) {
                streetSend.setChangeTime(DateUtils.getChangeTime(street.getChangeTime()));
            }
            task.getStreets().add(streetSend);
        }
    }


    private static void addChangeToStreet(List<TaskSend> taskSendList, Integer taskId, Integer streetId, ChangeSend change) {
        for (TaskSend taskSend : taskSendList) {
            if (taskSend.getTaskId().equals(taskId)) {
                for (StreetSend streetSend : taskSend.getStreets()) {
                    if (streetSend.getStreetId().equals(streetId)) {
                        streetSend.getChanges().add(change);
                    }
                }
            }
        }
    }

    private static void addBuildingIfNeeded(List<TaskSend> taskSendList, Integer taskId, Integer streetId, Building building) {
        boolean isExists = false;
        StreetSend street = null;
        for (TaskSend taskSend : taskSendList) {
            if (taskSend.getTaskId().equals(taskId)) {
                for (StreetSend streetSend : taskSend.getStreets()) {
                    if (streetSend.getStreetId().equals(streetId)) {
                        if (!isExists) {
                            street = streetSend;
                        }
                        for (BuildingSend buildingSend : streetSend.getHouses()) {
                            if (buildingSend.getHouseId().equals(building.getId())) {
                                isExists = true;
                            }
                        }
                    }
                }

            }
        }

        if (!isExists) {
            BuildingSend buildingSend = new BuildingSend(building.getId(), building.isNew() ? 1 : 0);
            if (building.getChangeTime() > 0) {
                buildingSend.setChangeTime(DateUtils.getChangeTime(building.getChangeTime()));
            }
            street.getHouses().add(buildingSend);
        }
    }

    private static void addChangeToBuilding(List<TaskSend> taskSendList, Integer taskId, Integer streetId, Integer buildingId, ChangeSend change) {
        for (TaskSend taskSend : taskSendList) {
            if (taskSend.getTaskId().equals(taskId)) {
                for (StreetSend streetSend : taskSend.getStreets()) {
                    if (streetSend.getStreetId().equals(streetId)) {
                        for (BuildingSend buildingSend : streetSend.getHouses()) {
                            if (buildingSend.getHouseId().equals(buildingId)) {
                                buildingSend.getChanges().add(change);
                            }
                        }
                    }
                }
            }
        }
    }


    private static void addApartmentIfNeeded(List<TaskSend> taskSendList, Integer taskId, Integer streetId, Integer buildingId, Apartment apartment) {
        boolean isExists = false;
        BuildingSend building = null;
        for (TaskSend taskSend : taskSendList) {
            if (taskSend.getTaskId().equals(taskId)) {
                for (StreetSend streetSend : taskSend.getStreets()) {
                    if (streetSend.getStreetId().equals(streetId)) {
                        for (BuildingSend buildingSend : streetSend.getHouses()) {
                            if (buildingSend.getHouseId().equals(buildingId)) {
                                if (!isExists) {
                                    building = buildingSend;
                                }
                                for (ApartmentSend apartmentSend : buildingSend.getFlats()) {
                                    if (apartmentSend.getFlatId().equals(apartment.getId())) {
                                        isExists = true;
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }

        if (!isExists) {
            ApartmentSend apartmentSend = new ApartmentSend(apartment.getId(), apartment.isNew() ? 1 : 0);
            if (apartment.getChangeTime() > 0) {
                apartmentSend.setChangeTime(DateUtils.getChangeTime(apartment.getChangeTime()));
            }
            building.getFlats().add(apartmentSend);
        }
    }

    private static void addChangeToApartment(List<TaskSend> taskSendList, Integer taskId, Integer streetId, Integer buildingId, Integer apartmentId, ChangeSend change) {
        for (TaskSend taskSend : taskSendList) {
            if (taskSend.getTaskId().equals(taskId)) {
                for (StreetSend streetSend : taskSend.getStreets()) {
                    if (streetSend.getStreetId().equals(streetId)) {
                        for (BuildingSend buildingSend : streetSend.getHouses()) {
                            if (buildingSend.getHouseId().equals(buildingId)) {
                                for (ApartmentSend apartmentSend : buildingSend.getFlats()) {
                                    if (apartmentSend.getFlatId().equals(apartmentId)) {
                                        apartmentSend.getChanges().add(change);
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }


    private static void cleanUpBeforeSend(List<TaskSend> list) {
        for (TaskSend taskSend : list) {
            for (StreetSend streetSend : taskSend.getStreets()) {
                if (streetSend.getIsNew().equals(1)) {
                    streetSend.setStreetId(-1);
                }
                for (BuildingSend buildingSend : streetSend.getHouses()) {
                    if (buildingSend.getIsNew().equals(1)) {
                        buildingSend.setHouseId(-1);
                    }
                    for (ApartmentSend apartmentSend : buildingSend.getFlats()) {
                        if (apartmentSend.getIsNew().equals(1)) {
                            apartmentSend.setFlatId(-1);
                        }
                    }
                }
            }
        }
    }


    private static void addObjectIfNeeded(List<TaskSend> taskSendList, History history, Context context, Integer userId) {
        Integer taskId = history.getTaskId();
        addTaskIfNeeded(taskSendList, taskId, context);

        Integer objectType = history.getObjectType();
        Integer changeType = history.getChangeType();

        Street street = null;
        Building building = null;
        Apartment apartment = null;

        switch (objectType) {
            case 1:
                street = RealmManager.getInstance().getStreet(taskId, history.getTempObjectId(), userId);
                addStreetIfNeeded(taskSendList, history.getTaskId(), street);
                addChangeToStreet(taskSendList, taskId, street.getId(), new ChangeSend(history.getChangeType(), history.getNewData().getData(), history.getOldData().getData()));
                break;
            case 2:
                building = RealmManager.getInstance().getBuilding(taskId, history.getTempObjectId(), userId);
                street = RealmManager.getInstance().getStreet(taskId, building.getStreetId(), userId);
                addStreetIfNeeded(taskSendList, history.getTaskId(), street);
                addBuildingIfNeeded(taskSendList, history.getTaskId(), street.getId(), building);
                addChangeToBuilding(taskSendList, taskId, street.getId(), building.getId(), new ChangeSend(history.getChangeType(), history.getNewData().getData(), history.getOldData().getData()));
                break;
            case 3:
                apartment = RealmManager.getInstance().getApartment(taskId, history.getTempObjectId(), userId);
                building = RealmManager.getInstance().getBuilding(taskId, apartment.getBuildingId(), userId);
                street = RealmManager.getInstance().getStreet(taskId, building.getStreetId(), userId);
                addStreetIfNeeded(taskSendList, history.getTaskId(), street);
                addBuildingIfNeeded(taskSendList, history.getTaskId(), street.getId(), building);
                addApartmentIfNeeded(taskSendList, history.getTaskId(), street.getId(), building.getId(), apartment);
                addChangeToApartment(taskSendList, taskId, street.getId(), building.getId(), apartment.getId(), new ChangeSend(history.getChangeType(), history.getNewData().getData(), history.getOldData().getData()));
                break;
        }
    }

}
