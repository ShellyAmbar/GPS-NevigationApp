package com.example.ambar.mygps;

import android.location.Location;

public class TaxiManager {
    private Location DestinationLocation;
    public void SetDestinationLocation(Location DestinationLocation) {
        this.DestinationLocation = DestinationLocation;
    };
    public float FindTheDistanceInMeter(Location CurrentLocation){
            if (CurrentLocation!=null && DestinationLocation!=null ){

                return CurrentLocation.distanceTo(DestinationLocation);

            }
            else{
              return (-100.0f);

            }

    }
    public String ReturnTheMileBetweenCurrentToDestination(Location CurrentLocation, int MeterPerMile){

        int miles=(int) (FindTheDistanceInMeter(CurrentLocation) /MeterPerMile);
        if(miles==1){return "1 Mile";}
        else if(miles>1){return miles + "  Miles"; }
        else{
           return "NO Mile";
        }

    }
    public String TimeToGetToDestinatio(Location CurrentLocation,float MilesPerHour , int MeterPerMile ){
        float Time= (FindTheDistanceInMeter(CurrentLocation) /MeterPerMile)/MilesPerHour;
        int HoursLeft= (int) Time;
        String TimeLeftString= "";
        if (HoursLeft== 1){

            TimeLeftString+=    "1 hour ";
        }else if (HoursLeft >1 ) {
            TimeLeftString+= HoursLeft + "Houres "; }


        int MinutesLeft= (int)(Time-HoursLeft)*60;

        if (MinutesLeft== 1){
            TimeLeftString+= "1 minute";
        }else if ( MinutesLeft>1){
            TimeLeftString+= MinutesLeft + "minutes"; }

        if(MinutesLeft<=0 && HoursLeft<=0 ){
            TimeLeftString+="Less Then A Minute Left";
        }

        return  TimeLeftString  ;

       }


    }







