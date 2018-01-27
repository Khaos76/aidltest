package pl.edu.pg.eti.pwta.s169301.aidl_test;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Admin on 27.01.2018.
 */

public class City implements Parcelable {
    public int x;
    public int y;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(x);
        out.writeInt(y);
    }

    public static final Parcelable.Creator<City> CREATOR
            = new Parcelable.Creator<City>() {
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        public City[] newArray(int size) {
            return new City[size];
        }
    };

    private City(Parcel in) {
        x = in.readInt();
        y = in.readInt();
    }

    public City(int x, int y) {
        this.x = x;
        this.y = y;
    }
        int getX() {
            return x;
        }

        int getY() {
            return y;
        }

    }
