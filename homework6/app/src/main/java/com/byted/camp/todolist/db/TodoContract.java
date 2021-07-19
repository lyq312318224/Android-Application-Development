package com.byted.camp.todolist.db;

import android.graphics.Color;
import android.provider.BaseColumns;

import com.byted.camp.todolist.beans.Priority;

public final class TodoContract {

    // TODO 1. 定义创建数据库以及升级的操作

    private TodoContract() {

    }
    public static class TodoNote implements BaseColumns{
        public static final String TABLIE_NAME="note";
        public static final String COLUMN_CONTENT="content";
        public static final String COLUMN_DATE="date";
        public static final String COLUMN_STATE="state";
        public static final String COLUMN_PRIORIYT="priority";
    }
    public static final String SQL_CREATE_ENTRIES=
            "CREATE TABLE "+TodoNote.TABLIE_NAME+"("+TodoNote._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +TodoNote.COLUMN_DATE+" INTEGER, " +TodoNote.COLUMN_STATE+" INTEGER "
            +TodoNote.COLUMN_CONTENT+" TEXT "+TodoNote.COLUMN_PRIORIYT+" INTEGER)";
    public static final String SQL_DELETE_ENTRIES="DROP TABLE IF EXISTS "+TodoNote.TABLIE_NAME;




}
