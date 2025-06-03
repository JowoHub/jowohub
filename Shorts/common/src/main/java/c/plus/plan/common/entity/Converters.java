package c.plus.plan.common.entity;

import androidx.room.TypeConverter;

import com.blankj.utilcode.util.GsonUtils;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class Converters {
    @TypeConverter
    public static HashMap<String, Integer> fromMap(String value) {
        try {
            Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
            return (HashMap<String, Integer>) GsonUtils.fromJson(value, type);
        } catch (JsonParseException e){

        }
        return null;
    }

    @TypeConverter
    public static String mapToJson(HashMap<String, Integer> value) {
        return GsonUtils.toJson(value);
    }
}
