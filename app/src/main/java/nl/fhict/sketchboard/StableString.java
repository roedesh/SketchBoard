package nl.fhict.sketchboard;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ruudschroen on 07-01-16.
 */
public class StableString implements Parcelable {
    private static long nextId = 0;
    private final long stableId;
    private String value;

    public StableString(String value) {
        value = value;
        stableId = nextId;
        nextId++;
    }

    public StableString(Parcel in) {
        value = in.readString();
        stableId = in.readLong();
    }

    public static final Parcelable.Creator<StableString> CREATOR =
            new Parcelable.Creator<StableString>() {
                @Override
                public StableString createFromParcel(Parcel in) {
                    return new StableString(in);
                }

                @Override
                public StableString[] newArray(int size) {
                    return new StableString[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(value);
        out.writeLong(stableId);
    }

    public String getValue() {
        return value;
    }

    public long getId() {
        return stableId;
    }
}