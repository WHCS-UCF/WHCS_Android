package com.whcs_ucf.whcs_android;

import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jimmy on 7/27/2015.
 */
public class CheckBoxGroup extends ArrayList<CheckBox> {
    boolean[] initialState;

    public void recordInitialState() {
        if(initialState != null) {
            return;
        }
        initialState = getCurrentState();
    }

    public boolean stateHasChanged() {
        if(initialState == null) {
            throw new Error("Can't check if the state of CheckBoxGroup changed if initial state was never initialized.");
        }

        boolean[] currentState = getCurrentState();
        if(currentState.length != initialState.length) {
            return true;
        }
        for(int i = 0; i < currentState.length; i++) {
            if(currentState[i] != initialState[i]) {
                return true;
            }
        }
        return false;
    }

    private boolean[] getCurrentState() {
        boolean[] state = new boolean[this.size()];
        for( int i = 0; i < this.size(); i++ ) {
            state[i] = this.get(i).isChecked();
        }
        return state;
    }

    public List<Integer> getCheckedIndexList() {
        ArrayList<Integer> checkedIndexes = new ArrayList<Integer>();
        for( int i = 0; i < this.size(); i++ ) {
            if(this.get(i).isChecked()) {
                checkedIndexes.add(i);
            }
        }
        return checkedIndexes;
    }

    public List<Integer> getCheckedIndexList(int indexOffset) {
        List<Integer> checkedIndexes = getCheckedIndexList();
        for( int i = 0; i < checkedIndexes.size(); i++ ) {
            checkedIndexes.set(i, indexOffset + checkedIndexes.get(i));
        }
        return checkedIndexes;
    }
}
