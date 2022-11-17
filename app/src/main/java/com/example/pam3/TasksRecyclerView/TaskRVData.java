package com.example.pam3.TasksRecyclerView;

public class TaskRVData {
    private int id;
    private String title;
    private String desc;
    private String category;
    private String creationDate;
    private String executionDate;
    private String executionTime;
    private int notification;
    private int status;
    private String imgPath;

    public TaskRVData(int id, String title, String desc, String category, String creationDate, String executionDate, String executionTime, int notification, int status, String imgPath) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.category = category;
        this.creationDate = creationDate;
        this.executionDate = executionDate;
        this.executionTime = executionTime;
        this.notification = notification;
        this.status = status;
        this.imgPath = imgPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getExecutionDate() {
        return executionDate;
    }

    public String getExecutionTime() {
        return executionTime;
    }

    public int getNotification() {
        return notification;
    }

    public int getStatus() {
        return status;
    }

    public String getImgPath() {
        return imgPath;
    }
}
