package com.pharos.walker.beans;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;

/**
 * Created by zhanglun on 2021/5/8
 * Describe:
 */
@Entity(nameInDb = "EVALUATE_VALUE")
public class EvaluateEntity implements Parcelable {
    @Id
    private Long id;
    @Index(unique = true)
    private long keyId;                 //数据库索引唯一ID
    private long userId;//用户唯一id
    private int evaluateResult;//评估结果
    private long createDate;//记录时间
    private long updateDate;//更新时间
    private int vas;//耐受等级
    private float firstValue;//做一次评估需要评估三次  第一次评估值
    private float secondValue;//第二次评估值
    private float thirdValue;//第三次评估值
    private float fourthValue;//第四次评估值
    private float fifthValue;//第五次评估值
    private int isUpload;                //上传状态  0-未上传  1-上传到局域网 2-上传到云端
    private String recordPath;
    @Generated(hash = 1029372824)
    public EvaluateEntity(Long id, long keyId, long userId, int evaluateResult,
            long createDate, long updateDate, int vas, float firstValue,
            float secondValue, float thirdValue, float fourthValue,
            float fifthValue, int isUpload, String recordPath) {
        this.id = id;
        this.keyId = keyId;
        this.userId = userId;
        this.evaluateResult = evaluateResult;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.vas = vas;
        this.firstValue = firstValue;
        this.secondValue = secondValue;
        this.thirdValue = thirdValue;
        this.fourthValue = fourthValue;
        this.fifthValue = fifthValue;
        this.isUpload = isUpload;
        this.recordPath = recordPath;
    }
    @Generated(hash = 375544307)
    public EvaluateEntity() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getKeyId() {
        return this.keyId;
    }
    public void setKeyId(long keyId) {
        this.keyId = keyId;
    }
    public long getUserId() {
        return this.userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    public int getEvaluateResult() {
        return this.evaluateResult;
    }
    public void setEvaluateResult(int evaluateResult) {
        this.evaluateResult = evaluateResult;
    }
    public long getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
    public long getUpdateDate() {
        return this.updateDate;
    }
    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }
    public int getVas() {
        return this.vas;
    }
    public void setVas(int vas) {
        this.vas = vas;
    }
    public float getFirstValue() {
        return this.firstValue;
    }
    public void setFirstValue(float firstValue) {
        this.firstValue = firstValue;
    }
    public float getSecondValue() {
        return this.secondValue;
    }
    public void setSecondValue(float secondValue) {
        this.secondValue = secondValue;
    }
    public float getThirdValue() {
        return this.thirdValue;
    }
    public void setThirdValue(float thirdValue) {
        this.thirdValue = thirdValue;
    }
    public float getFourthValue() {
        return this.fourthValue;
    }
    public void setFourthValue(float fourthValue) {
        this.fourthValue = fourthValue;
    }
    public float getFifthValue() {
        return this.fifthValue;
    }
    public void setFifthValue(float fifthValue) {
        this.fifthValue = fifthValue;
    }
    public int getIsUpload() {
        return this.isUpload;
    }
    public void setIsUpload(int isUpload) {
        this.isUpload = isUpload;
    }
    public String getRecordPath() {
        return this.recordPath;
    }
    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeLong(this.keyId);
        dest.writeLong(this.userId);
        dest.writeInt(this.evaluateResult);
        dest.writeLong(this.createDate);
        dest.writeLong(this.updateDate);
        dest.writeInt(this.vas);
        dest.writeFloat(this.firstValue);
        dest.writeFloat(this.secondValue);
        dest.writeFloat(this.thirdValue);
        dest.writeFloat(this.fourthValue);
        dest.writeFloat(this.fifthValue);
        dest.writeInt(this.isUpload);
        dest.writeString(this.recordPath);
    }

    protected EvaluateEntity(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.keyId = in.readLong();
        this.userId = in.readLong();
        this.evaluateResult = in.readInt();
        this.createDate = in.readLong();
        this.updateDate = in.readLong();
        this.vas = in.readInt();
        this.firstValue = in.readFloat();
        this.secondValue = in.readFloat();
        this.thirdValue = in.readFloat();
        this.fourthValue = in.readFloat();
        this.fifthValue = in.readFloat();
        this.isUpload = in.readInt();
        this.recordPath = in.readString();
    }

    public static final Creator<EvaluateEntity> CREATOR = new Creator<EvaluateEntity>() {
        @Override
        public EvaluateEntity createFromParcel(Parcel source) {
            return new EvaluateEntity(source);
        }

        @Override
        public EvaluateEntity[] newArray(int size) {
            return new EvaluateEntity[size];
        }
    };
}
