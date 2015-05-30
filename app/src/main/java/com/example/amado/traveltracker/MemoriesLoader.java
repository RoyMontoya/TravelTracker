package com.example.amado.traveltracker;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by Amado on 29/05/2015.
 */
public class MemoriesLoader extends DbCursorLoader {

    private MemoriesDataSource mDataSource;

    public MemoriesLoader(Context context, MemoriesDataSource memoriesDataSource) {
        super(context);
        mDataSource = memoriesDataSource;
    }

    @Override
    protected Cursor LoadCursor() {
        return mDataSource.allMemoriesCursor();
    }
}
