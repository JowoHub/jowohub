package c.plus.plan.shorts.entity;

import androidx.room.TypeConverter;

import com.blankj.utilcode.util.GsonUtils;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class Converters {
    @TypeConverter
    public static PurchaseProduct.Benefit fromBenefit(String value) {
        try {
            Type type = new TypeToken<PurchaseProduct.Benefit>(){}.getType();
            return (PurchaseProduct.Benefit) GsonUtils.fromJson(value, type);
        } catch (JsonParseException e){

        }
        return null;
    }

    @TypeConverter
    public static String benefitToJson(PurchaseProduct.Benefit value) {
        return GsonUtils.toJson(value);
    }
}
