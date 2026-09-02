package com.electrovotesuperx.model.AdminModel;

import javafx.scene.control.Label;

public class AdminDash {

    int electionValue;
    int voterValue;
    int votesValue;
    double turnoutValue;

    AdminDash(int electionValue,int voterValue,int votesValue,double turnoutValue){
        this.electionValue = electionValue;
        this.voterValue = voterValue;
        this.votesValue = votesValue;
        this.turnoutValue= turnoutValue;
    }

    public int getElectionValue() {
        return electionValue;
    }

    public void setElectionValue(int electionValue) {
        this.electionValue = electionValue;
    }

    public int getVoterValue() {
        return voterValue;
    }

    public void setVoterValue(int voterValue) {
        this.voterValue = voterValue;
    }

    public int getVotesValue() {
        return votesValue;
    }

    public void setVotesValue(int votesValue) {
        this.votesValue = votesValue;
    }

    public double getTurnoutValue() {
        return turnoutValue;
    }

    public void setTurnoutValue(double turnoutValue) {
        this.turnoutValue = turnoutValue;
    }
    
    
}
