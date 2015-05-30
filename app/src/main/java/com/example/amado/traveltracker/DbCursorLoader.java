package com.example.amado.traveltracker;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by Amado on 29/05/2015.
 */
public abstract class DbCursorLoader extends AsyncTaskLoader<Cursor> {
    private Cursor mCursor;

    public DbCursorLoader(Context context){
        super(context);
    }

    protected abstract Cursor LoadCursor();

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = LoadCursor();
        if(cursor !=null){
            cursor.getCount();
        }
        return cursor;
    }

    @Override
    public void deliverResult(Cursor data) {
        Cursor oldCursor = mCursor;
        mCursor=data;
        if(isStarted()){
            super.deliverResult(data);
        }
        if(oldCursor != null && oldCursor != data){
            onreleaseResources(oldCursor);
        }
    }


    @Override
    protected void onStartLoading() {
        if(mCursor!=null){
            deliverResult(mCursor);
        }
        if(takeContentChanged()|| mCursor ==null){
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor data) {
        super.onCanceled(data);
        if(data != null){
            onreleaseResources(data);
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStartLoading();
        if(mCursor!= null){
            onreleaseResources(mCursor);
        }
        mCursor = null;
    }

    private void onreleaseResources(Cursor cursor){
        if(!cursor.isClosed())
        cursor.close();
    }

}
