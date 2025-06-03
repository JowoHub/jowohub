package c.plus.plan.common.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.IntDef;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Objects;

import c.plus.plan.common.R;

/**
 * Created by fanwei on 1/23/22
 */
@Entity(tableName = "users")
public class User implements Parcelable {
    @PrimaryKey
    private long id;
    private String avatar;
    private int gender;
    private String nickname;
    private int coin;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.avatar);
        dest.writeInt(this.gender);
        dest.writeString(this.nickname);
        dest.writeInt(this.coin);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readLong();
        this.avatar = source.readString();
        this.gender = source.readInt();
        this.nickname = source.readString();
        this.coin = source.readInt();
    }

    public User() {
    }

    protected User(Parcel in) {
        this.id = in.readLong();
        this.avatar = in.readString();
        this.gender = in.readInt();
        this.nickname = in.readString();
        this.coin = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
