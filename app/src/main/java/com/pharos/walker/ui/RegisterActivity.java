package com.pharos.walker.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.ListPopupWindow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.beans.ActivationCodeBean;
import com.pharos.walker.beans.DoctorBean;
import com.pharos.walker.beans.DoctorTeamBean;
import com.pharos.walker.beans.HospitalBean;
import com.pharos.walker.beans.LocalDoctorNameBean;
import com.pharos.walker.beans.TokenBean;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.constants.Api;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.customview.MyDatePickerDialog;
import com.pharos.walker.customview.popupdialog.PopupSheet;
import com.pharos.walker.customview.popupdialog.PopupSheetCallback;
import com.pharos.walker.customview.rxdialog.RxDialogDiagnosisSelect;
import com.pharos.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharos.walker.customview.rxdialog.RxImageDialog;
import com.pharos.walker.customview.rxdialog.RxUserInfoDialog;
import com.pharos.walker.database.ActivationCodeManager;
import com.pharos.walker.database.LocalDoctorNameManager;
import com.pharos.walker.database.UserManager;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.DimensUtil;
import com.pharos.walker.utils.IdCard;
import com.pharos.walker.utils.NetworkUtils;
import com.pharos.walker.utils.OkHttpUtils;
import com.pharos.walker.utils.QrUtil;
import com.pharos.walker.utils.RegexUtils;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.SnowflakeIdUtil;
import com.pharos.walker.utils.ToastUtils;
import com.tencent.mars.xlog.Log;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.json.JSONObject;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.util.ConvertUtils;
import okhttp3.Request;

/**
 * Created by zhanglun on 2021/6/1
 * Describe:
 */
public class RegisterActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_step_1)
    TextView tvStep1;
    @BindView(R.id.tv_step_2)
    TextView tvStep2;
    @BindView(R.id.tv_step_3)
    TextView tvStep3;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.iv_pwd_see)
    ImageView ivPwdSee;
    @BindView(R.id.et_confirm_password)
    EditText etConfirmPassword;
    @BindView(R.id.iv_confirm_pwd_see)
    ImageView ivConfirmPwdSee;
    @BindView(R.id.tv_step_1_ok)
    TextView tvStep1Ok;
    @BindView(R.id.layout_sub_step_1)
    LinearLayout layoutSubStep1;
    @BindView(R.id.layout_step_1)
    RelativeLayout layoutStep1;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_age)
    EditText etAge;
    @BindView(R.id.et_weight)
    EditText etWeight;
    @BindView(R.id.rb_male)
    RadioButton rbMale;
    @BindView(R.id.rb_female)
    RadioButton rbFemale;
    @BindView(R.id.rg_sex)
    RadioGroup rgSex;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_step_2_previous)
    TextView tvStep2Previous;
    @BindView(R.id.tv_step_2_ok)
    TextView tvStep2Ok;
    @BindView(R.id.layout_step_2)
    LinearLayout layoutStep2;
    @BindView(R.id.tv_hospital_address)
    TextView tvHospitalAddress;
    @BindView(R.id.et_hospital_name)
    EditText etHospitalName;
    @BindView(R.id.tv_doctor)
    TextView tvDoctor;
    @BindView(R.id.tv_step_3_previous)
    TextView tvStep3Previous;
    @BindView(R.id.tv_step_3_finish)
    TextView tvStep3Finish;
    @BindView(R.id.layout_step_3)
    LinearLayout layoutStep3;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.et_CaseHistoryNum)
    EditText etCaseHistoryNum;
    @BindView(R.id.et_doctor)
    EditText etDoctor;
    @BindView(R.id.sp_select_diagnostic_result)
    Spinner spinner;
    @BindView(R.id.cb_password_manager)
    CheckBox cbPasswordManager;
    @BindView(R.id.tv_step_skip)
    TextView tvStepSkip;
    @BindView(R.id.tv_card_type)
    TextView tvCardType;
    @BindView(R.id.tv_card)
    TextView tvCard;
    @BindView(R.id.img_qr)
    ImageView imgQr;
    @BindView(R.id.rl_diagnostic_select)
    RelativeLayout rlDiagnostic;
    @BindView(R.id.tv_select_diagnostic_result)
    TextView tvSelectDiagnosticResult;
    @BindView(R.id.tv_disease_diagnosis)
    TextView tvDiseaseDiagnosis;
    @BindView(R.id.tv_select_height_result)
    TextView tvSelectHeightResult;
    @BindView(R.id.rl_height_select)
    RelativeLayout rlHeightSelect;
    @BindView(R.id.tv_education_level)
    TextView tvEducationLevel;
    @BindView(R.id.title_register)
    TextView titleRegister;
    @BindView(R.id.rl_education_level)
    RelativeLayout rlEducationLevel;
    @BindView(R.id.tv_doctor_team)
    TextView tvDoctorTeam;
    private String provinceName;
    private String cityName;
    private String countyName;
    private int sex = 1; //性别  男-1  女-0
    private String account;
    private String password;
    private String name;
    private String age;
    private String weight;
    private String city;
    private String doctor;
    private String mobile;
    private String openid;
    private List<Province> provincesList;
    private List<Province> operationList;
    private AlertDialog provinceDialog;
    private Context context;
    private boolean isHidenPwd = true;
    private boolean isHidenConfirmPwd = true;
    private MyDatePickerDialog mDatePickerDialog;
    private UserBean userBean;
    private String hospitalAddress;
    private String hospitalName;
    private int selectPosition;
    private String dateOfSurgery;
    private int SELECT_PERSON_ADDRESS = 0;
    private int SELECT_HOSPITAL_ADDRESS = 1;
    private int HOSPITAL_REQ = 1;
    private int DOCTOR_REQ = 2;
    private int USER_REQ = 3;
    private int NEW_USER = 4;
    private int hospitalId = 0;
    private long userId = 0;
    private String cardType = "身份证";
    private List<String> doctorList = new ArrayList<>();
    private LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
    private String otherDiagnosis;
    private int reConnected = 1;
    private String[] array;
    private String[] arrayDisease;
    private String[] arrayEducationLevel;
    private PopupSheet popupSheet;
    private String diseaseName;
    private String educationLevel;
    private String height;
    private String teamName;
    private int teamId;
    private List<String> heightList = null;
    private final static int onlineUser = 0;
    private final static int offlineUser = 1;
    private  ActivationCodeBean codeBean;
    private List<String> teamList = new ArrayList<>();
    private List<Integer> teamIdList = new ArrayList<>();
    private String bodyPartName;//部位
    private String secondDiseaseName;//疾病名称
    private String treatmentMethodName;//治疗方式
    private String treatmentMethodId;//治疗方式Id
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        context = this;
        codeBean = ActivationCodeManager.getInstance().getCodeBean();
        initView();
        initData();
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void initView() {
//        tvCardType.setText(cardType);
        etPassword.setFocusableInTouchMode(false);
        etConfirmPassword.setFocusableInTouchMode(false);
        etPassword.setHintTextColor(getColor(R.color.white_30));
        etConfirmPassword.setHintTextColor(getColor(R.color.white_30));
        cbPasswordManager.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etPassword.setFocusableInTouchMode(true);
                    etConfirmPassword.setFocusableInTouchMode(true);
                    etConfirmPassword.requestFocus();
                    etPassword.requestFocus();
                    etPassword.setHintTextColor(getColor(R.color.white_88));
                    etConfirmPassword.setHintTextColor(getColor(R.color.white_88));
                } else {
                    etPassword.setFocusableInTouchMode(false);
                    etConfirmPassword.setFocusableInTouchMode(false);
                    etPassword.clearFocus();
                    etConfirmPassword.clearFocus();
                    etPassword.setHintTextColor(getColor(R.color.white_30));
                    etConfirmPassword.setHintTextColor(getColor(R.color.white_30));
                }
            }
        });
        rgSex.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_male:
                    sex = 1;
                    break;
                case R.id.rb_female:
                    sex = 0;
                    break;
            }
        });

        initDataTime();
//        if (!TextUtils.isEmpty(SPHelper.getSystemSettingHospitalName())){
//            tvHospitalName.setText(SPHelper.getSystemSettingHospitalName());
//        }
        if (!TextUtils.isEmpty(SPHelper.getSystemSettingHospitalName())) {
            etHospitalName.setText(SPHelper.getSystemSettingHospitalName());
        }
        generateQrCode();
    }

    private void initData() {
        if (userBean == null) {
            userBean = new UserBean();
        }
        List<LocalDoctorNameBean> list = LocalDoctorNameManager.getInstance().loadAll();
        for (LocalDoctorNameBean localDoctorNameBean : list) {
            linkedHashSet.add(localDoctorNameBean.getDoctorName());
        }
        Resources res = getResources();
        if (SPHelper.getOperationSwitch()) {
            array = res.getStringArray(R.array.diagnostic_result_list_1);
        } else {
            array = res.getStringArray(R.array.diagnostic_result_list);
        }
        arrayDisease = res.getStringArray(R.array.disease_diagnostic);
        arrayEducationLevel = res.getStringArray(R.array.education_level_select_list);
        if (SPHelper.getWeightLimitSwitch()) {
            Global.minWeight = 30;
            Global.maxWeight = 130;
        } else {
            Global.minWeight = 35;
            Global.maxWeight = 100;
        }
        if (heightList == null) {
            heightList = new ArrayList<>();
            for (int i = 120; i < 200; i++) {
                heightList.add(i+"cm");
            }
        }
        teamList.add("默认医生团队");
        teamIdList.add(0);
        getDoctorTeam();
        getHospitalName();
    }

    private void getDoctorTeam(){
        OkHttpUtils.getAsync(Api.getDoctorTeam+"?macAdd=" + codeBean.getMacAddress(), true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                android.util.Log.e("Register", "获取医生团队: "+ e);
                if (teamList.size()<=0){
                    teamList.add("默认医生团队");
                    teamIdList.add(0);
                }
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                android.util.Log.e("Register", "获取医生团队: "+ result);
                DoctorTeamBean doctorTeamBean =  new Gson().fromJson(result, DoctorTeamBean.class);
                if (doctorTeamBean.getData() != null){
                    teamList.clear();
                    teamIdList.clear();
                    for (DoctorTeamBean.DataBean dataBean:doctorTeamBean.getData()){
                        teamList.add(dataBean.getName());
                        teamIdList.add(dataBean.getId());
                    }
                    if (teamList.size()<=0){
                        teamList.add("默认医生团队");
                        teamIdList.add(0);
                    }
                }else {
                    if (teamList.size()<=0){
                        teamList.add("默认医生团队");
                        teamIdList.add(0);
                    }
                }


            }
        });
    }
    private void getHospitalName(){
        OkHttpUtils.getAsync(Api.getHospitalNameByMacAddress+"?macAddress=" + codeBean.getMacAddress(), true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                android.util.Log.e("Register", "获取医院名称: "+ e);
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                android.util.Log.e("Register", "获取医院名称: "+ result);
                if (result.length() > "{\"code\":0}".length()){
                    JSONObject toJsonObj = new JSONObject(result);
                    JSONObject dataJson = new JSONObject(toJsonObj.getString("data"));
                    hospitalName = dataJson.getString("name");
                    etHospitalName.setText(hospitalName);
                    SPHelper.saveSystemSettingHospitalName(hospitalName);
                }

//               DoctorTeamBean doctorTeamBean =  new Gson().fromJson(result, DoctorTeamBean.class);
//               if (doctorTeamBean.getData() != null){
//                   for (DoctorTeamBean.DataBean dataBean:doctorTeamBean.getData()){
//                       teamList.add(dataBean.getName());
//                       teamIdList.add(dataBean.getId());
//                   }
//                   if (teamList.size()<=0){
//                       teamList.add("默认医生团队");
//                       teamIdList.add(0);
//                   }
//               }else {
//                   if (teamList.size()<=0){
//                       teamList.add("默认医生团队");
//                       teamIdList.add(0);
//                   }
//               }


            }
        });
    }
    private void generateQrCode() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float density = metrics.density;
        android.util.Log.e("Register", "generateQrCode: " + metrics.densityDpi);
        int qrWidth = (int) (260 * density);

        Resources res = getResources();
        Bitmap logoBitmap = BitmapFactory.decodeResource(res, R.mipmap.ic_launcher);
//        String content = Api.qrUrl + SPHelper.getUserId();
        String content = Api.qrRegisterUrl + codeBean.getMacAddress();
        Bitmap qrBitmap = QrUtil.createQRCodeBitmap(content, qrWidth, qrWidth, "UTF-8", "H", "1", Color.BLACK, Color.WHITE, logoBitmap, 0.15F);
        imgQr.setImageBitmap(qrBitmap);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_register;
    }

    @OnClick({R.id.iv_back, R.id.iv_pwd_see, R.id.tv_step_1_ok, R.id.tv_location, R.id.tv_hospital_address, R.id.tv_doctor, R.id.tv_card_type,
            R.id.tv_step_2_previous, R.id.tv_step_3_previous, R.id.tv_step_3_finish, R.id.tv_step_2_ok, R.id.iv_confirm_pwd_see, R.id.tv_date, R.id.tv_step_skip, R.id.img_doctor_select,
            R.id.ll_select_doctor, R.id.rl_diagnostic_select, R.id.rl_education_level,R.id.rl_height_select,R.id.tv_select_height_result,R.id.img_height_select,R.id.tv_disease_diagnosis,
            R.id.tv_doctor_team,R.id.title_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_pwd_see:
                if (isHidenPwd) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//显示密码
                    ivPwdSee.setImageResource(R.drawable.ic_pwd_enable);
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
                    ivPwdSee.setImageResource(R.drawable.ic_pwd_unable);
                }
                isHidenPwd = !isHidenPwd;
                break;
            case R.id.tv_step_1_ok:
                firstCommit();
                break;
            case R.id.tv_step_skip:
                etAccount.setText("");
                etConfirmPassword.setText("");
                etPassword.setText("");
                setDisplayView(layoutStep2, tvStep2);
                break;
            case R.id.tv_hospital_address:
                addressSelect(SELECT_HOSPITAL_ADDRESS);
                break;
            case R.id.tv_location:
                addressSelect(SELECT_PERSON_ADDRESS);
                break;
//            case R.id.tv_hospital_name:
//                if (NetworkUtils.isConnected()) {
//                    getHospitalByAddress();
//                } else {
//                    ToastUtils.showShort("网络未连接");
//                }
//                break;
            case R.id.tv_step_3_previous:
                setDisplayView(layoutStep2, tvStep2);
                break;
            case R.id.tv_step_3_finish:
                finishCommit();
                break;
            case R.id.tv_step_2_previous:
                setDisplayView(layoutStep1, tvStep1);
                break;
            case R.id.tv_step_2_ok:
                secondCommit();
                break;
            case R.id.iv_confirm_pwd_see:
                if (isHidenConfirmPwd) {
                    etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//显示密码
                    ivConfirmPwdSee.setImageResource(R.drawable.ic_pwd_enable);
                } else {
                    etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
                    ivConfirmPwdSee.setImageResource(R.drawable.ic_pwd_unable);
                }
                isHidenConfirmPwd = !isHidenConfirmPwd;
                break;
            case R.id.tv_date:
                if (mDatePickerDialog != null) {
                    mDatePickerDialog.show();
                }
                break;
            case R.id.tv_doctor:
            case R.id.img_doctor_select:
            case R.id.ll_select_doctor:
                selectDoctor();
                break;
            case R.id.tv_card_type:
                selectCardType();
                break;
            case R.id.rl_diagnostic_select:
//                selectedSurgeryName();
                operationSelect();
                break;
            case R.id.tv_disease_diagnosis:
                selectedDiseaseDiagnosis();
                break;
            case R.id.rl_height_select:
            case R.id.img_height_select:
            case R.id.tv_select_height_result:
                selectedHeight();
                break;
            case R.id.rl_education_level:
            case R.id.tv_education_level:
                selectedEducationLevel();
                break;
            case R.id.tv_doctor_team:
                selectedDoctorTeam();
                break;
            case R.id.title_register:
//                mockCreateUser();
                break;
        }
    }


    private void firstCommit() {
        account = etAccount.getText().toString().trim();
//        if (TextUtils.isEmpty(account)) {
//            ToastUtils.showShort("请输入用户名");
//            return;
//        }
//        if (account.length() < 2 || account.length() > 25) {
//            ToastUtils.showShort("用户名为2-25位字符");
//            return;
//        }

        password = etPassword.getText().toString().trim();
//        if (TextUtils.isEmpty(password)) {
//            ToastUtils.showShort("请输入密码");
//            return;
//        }
        if (!TextUtils.isEmpty(password) && password.length() < 4 || password.length() > 20) {
            ToastUtils.showShort("密码为4-20位字符");
            return;
        }

        String confirm_password = etConfirmPassword.getText().toString().trim();
//        if (TextUtils.isEmpty(confirm_password)) {
//            ToastUtils.showShort("请确认密码");
//            return;
//        }
        if (!password.equals(confirm_password)) {
            ToastUtils.showShort("两次密码输入不一致");
            return;
        }
        setDisplayView(layoutStep2, tvStep2);
    }

    private void secondCommit() {
        name = etName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showShort("请输入姓名");
            return;
        }
        age = etAge.getText().toString().trim();
        if (TextUtils.isEmpty(age)) {
            ToastUtils.showShort("请输入年龄");
            return;
        }
        if (Integer.parseInt(age) < 1 || Integer.parseInt(age) > 150) {
            ToastUtils.showShort("年龄不符合范围");
            return;
        }
        weight = etWeight.getText().toString().trim();
        if (TextUtils.isEmpty(weight)) {
            ToastUtils.showShort("请输入体重");
            return;
        }
        if (Integer.parseInt(weight) < Global.minWeight || Integer.parseInt(weight) > Global.maxWeight) {
            ToastUtils.showShort(MessageFormat.format("体重必须在{0}kg到{1}kg之间", Global.minWeight, Global.maxWeight));
            return;
        }
//        if (TextUtils.isEmpty(height)) {
//            ToastUtils.showShort("请输入身高");
//            return;
//        }
//        if (TextUtils.isEmpty(city)) {
//            ToastUtils.showShort("请选择个人所在地");
//            return;
//        }
        if (TextUtils.isEmpty(dateOfSurgery)) {
            ToastUtils.showShort("请选择住院时间");
            return;
        }
//        if (TextUtils.isEmpty(educationLevel)) {
//            ToastUtils.showShort("请输入文化程度");
//            return;
//        }
//        if (selectPosition <= 0 || (selectPosition == array.length - 1 && TextUtils.isEmpty(otherDiagnosis))) {
//            ToastUtils.showShort("请选择手术名称");
//            return;
//        }
//        if (TextUtils.isEmpty(tvSelectDiagnosticResult.getText().toString())) {
//            ToastUtils.showShort("请选择手术名称");
//            return;
//        }
        setDisplayView(layoutStep3, tvStep3);
    }

    private void mockCreateUser() {
        operationList = new ArrayList<>();
        try {
            String json = ConvertUtils.toString(getAssets().open("operation_name.json"));
            operationList.addAll(new Gson().fromJson(json, new TypeToken<List<Province>>() {
            }.getType()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> methonNameList = new ArrayList<>();
        for (Province province:operationList){
            List<City> citiesList = province.getCities();
            for (City city : citiesList) {
                List<County> countiesList = city.getThirds();
                for (County county : countiesList) {
                    methonNameList.add(county.getAreaName());
                }
            }
        }
//        System.out.println(methonNameList);
        for (int i= 0;i<37;i++){
            UserBean userBean = new UserBean();
            userBean.setMacAdd(codeBean.getMacAddress());
            userBean.setName("法罗适0"+(30+i));
            userBean.setUserId(SnowflakeIdUtil.getUniqueId());
            userBean.setHeight("170cm");
            userBean.setWeight("60");
            userBean.setCardType(0);
            String idCard = IdCard.getIdNo(false);
            userBean.setIdCard(idCard);
            userBean.setCaseHistoryNo(idCard);
            userBean.setDate("2023-04-10 00:00:00");
            userBean.setAge(40);
            userBean.setSex(1);
            userBean.setDoctorTeam("默认团队");
            userBean.setDiagnosis(methonNameList.get(29+i));
            userBean.setTreatmentMethodId(String.valueOf(30+i));
            userBean.setCreateDate(DateFormatUtil.getNowDate());
            userBean.setEducationLevel("大学本科");
            UserManager.getInstance().insert(userBean,0);

        }
        ToastUtils.showShort("生成成功");

    }
    private void finishCommit() {
        if (TextUtils.isEmpty(etCaseHistoryNum.getText().toString())) {
            ToastUtils.showShort("请输入证件号");
            return;
        }
        if (cardType.equals("身份证") && !RegexUtils.isIDCard18Exact(etCaseHistoryNum.getText())) {
            ToastUtils.showShort("证件号有误");
            return;
        }
//        if (TextUtils.isEmpty(teamName)) {
//            ToastUtils.showShort("请选择医生团队");
//            return;
//        }
        if (cardType.equals("身份证")) {
            userBean.setCardType(0);
            userBean.setIdCard(etCaseHistoryNum.getText().toString());
        } else {
            userBean.setCardType(1);
        }
        userBean.setCaseHistoryNo(etCaseHistoryNum.getText().toString());
        userBean.setWeight(weight);
        userBean.setAccount(account);
        userBean.setPassword(password);
        userBean.setAddress(city);
        userBean.setAge(Integer.parseInt(age));
        userBean.setDoctorTeam(teamName);
        userBean.setDoctorTeamId(teamId);
        userBean.setSecondDiseaseName(secondDiseaseName);
        userBean.setBodyPartName(bodyPartName);
        if (TextUtils.isEmpty(hospitalAddress)) {
            hospitalAddress = SPHelper.getHospitalAddress();
        }
        userBean.setHospitalAddress(hospitalAddress);
//        if (TextUtils.isEmpty(hospitalName)) {
//            hospitalName = SPHelper.getHospitalName();
//        }
        if (!TextUtils.isEmpty(etHospitalName.getText().toString()) && !etHospitalName.getText().toString().contains("请选择")) {
            hospitalName = etHospitalName.getText().toString();
        }
        userBean.setHospitalName(hospitalName);
        if (!TextUtils.isEmpty(etDoctor.getText().toString())) {
            doctor = etDoctor.getText().toString();
            LocalDoctorNameBean doctorNameBean = new LocalDoctorNameBean();
            doctorNameBean.setDoctorName(doctor);
            doctorNameBean.setKeyId(SnowflakeIdUtil.getUniqueId());
            doctorNameBean.setCreateDate(System.currentTimeMillis());
            if (codeBean != null) {
                doctorNameBean.setMacAddress(codeBean.getMacAddress());
            }
            LocalDoctorNameManager.getInstance().insert(doctorNameBean);
        }
        userBean.setDoctor(doctor);
        userBean.setName(name);
        userBean.setSex(sex);
//        if (selectPosition == array.length - 1 && !TextUtils.isEmpty(otherDiagnosis)) {
//            userBean.setDiagnosis(otherDiagnosis);
//        } else {
//            userBean.setDiagnosis(array[selectPosition]);
//        }
//        userBean.setDiagnosis(tvSelectDiagnosticResult.getText().toString());
        userBean.setDiagnosis("其他(康复)）");
        userBean.setTreatmentMethodId("1012");
        userBean.setDiseaseDiagnosis(diseaseName);
        userBean.setTelephone(mobile);
        userBean.setHeight(height);
        userBean.setEducationLevel(educationLevel);
        userId = SnowflakeIdUtil.getUniqueId();
        userBean.setUserId(userId);
        userBean.setCreateDate(DateFormatUtil.getNowDate());
        userBean.setUserType(1);
        if (!UserManager.getInstance().isUniqueValue(etCaseHistoryNum.getText().toString(), 0)) {
            ToastUtils.showShort("证件号重复了，请修改");
            return;
        }
        if (NetworkUtils.isConnected()) {
            showWaiting("提示", "正在注册...");
            if (userBean.getCardType() == 0) {//判断是否是身份证注册
                syncUser();
            } else {//病例号注册不验证后台
                int saveStatus = saveUser(userBean, onlineUser);
                if (saveStatus == 0) {
                    syncNewUser();
                }
            }
        } else {
            ToastUtils.showShort("网络不可用");
            netErrorDialog();
//            goEvaluateDialog();
        }
    }

    private int saveUser(UserBean userBean, int userType) {
        if (UserManager.getInstance().isUniqueValue(etCaseHistoryNum.getText().toString(), 0)) {
            try {
                UserManager.getInstance().insert(userBean, 0);
                if (userType == offlineUser) {
                    goEvaluateDialog();
                }
            } catch (SQLiteConstraintException e) {
                if (e.getMessage().contains("USER.CASE_HISTORY_NO")) {
                    ToastUtils.showShort("证件号重复了，请修改");
                }
                return 1;
            }
        } else {
            ToastUtils.showShort("证件号重复了，请修改");
            return 1;
        }
        Global.USER_MODE = true;
        return 0;
    }

    private void netErrorDialog() {
        RxDialogSureCancel rxDialog = new RxDialogSureCancel(this);
        rxDialog.setContent("网络不可用，请尝试重试");
        rxDialog.setCancel("脱机注册");
        rxDialog.setSure("重试");
        rxDialog.setCancelable(false);
        rxDialog.setCanceledOnTouchOutside(false);
        rxDialog.setSureListener(v -> {
            if (NetworkUtils.isConnected()) {
                showWaiting("获取数据", "正在请求……");
                if (userBean.getCardType() == 0) {//判断是否是身份证注册
                    syncUser();
                } else {//病例号注册不验证后台
                    int saveStatus = saveUser(userBean, onlineUser);
                    if (saveStatus == 0) {
                        syncNewUser();
                    }
                }
            } else {
                netErrorDialog();
            }
            rxDialog.dismiss();
        });
        rxDialog.setCancelListener(v -> {
            saveUser(userBean, offlineUser);
            rxDialog.dismiss();
        });
        rxDialog.show();
    }

    private void registerUserDialog(UserBean userBeanServices, String content, String diseaseName) {
        RxUserInfoDialog rxDialog = new RxUserInfoDialog(this);
        rxDialog.setTitle("已存在一个相同身份证号的用户");
        rxDialog.setContent(content);
        rxDialog.setDiagnosis(diseaseName);
        rxDialog.setCancel("注册新账号");
        rxDialog.setSure("使用旧账号");
        rxDialog.setCancelable(false);
        rxDialog.setCanceledOnTouchOutside(false);
        rxDialog.setSureListener(v -> {
            int saveStatus = saveUser(userBeanServices, onlineUser);
            if (saveStatus == 0) {
                userId = userBeanServices.getUserId();
                UserManager.getInstance().updateUserUploadStatus(userBeanServices, Global.UploadNetStatus);
                goEvaluateWithQrCodeDialog();
            }
            rxDialog.dismiss();

        });
        rxDialog.setCancelListener(v -> {
            int saveStatus = saveUser(userBean, onlineUser);
            if (saveStatus == 0) {
                syncNewUser();
            }
            rxDialog.dismiss();
        });
        rxDialog.show();
    }

    private void syncUser() {
//        List<UserBean> userBeans = UserManager.getInstance().loadNoNetUploadUser();
        List<UserBean> userBeans = new ArrayList<>();
        userBean.setMacAdd(codeBean.getMacAddress());
        userBeans.add(userBean);
        String data = new Gson().toJson(userBeans);
        OkHttpUtils.postJsonAsync(Api.uploadUserRegister, data, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                if (reConnected > 0) {
                    syncUser();
                    reConnected--;
                } else {
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
                    ToastUtils.showShort("服务器访问错误");

                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    saveUser(userBean, offlineUser);
                }
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Setting Activity", "--->用户同步结果" + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_USER_RESULT, code));
                if (code == 0) {
                    if (result.contains("data")) {
                        String jsonData = toJsonObj.getString("data");
                        UserBean userBeanService = new Gson().fromJson(jsonData, UserBean.class);
                        String content = String.format("旧账号信息\n用户名：%s\n年龄：%s\n体重：%s\n手术时间：%s",
                                userBeanService.getName(),
                                userBeanService.getAge(),
                                userBeanService.getWeight(),
                                userBeanService.getDate().substring(0, userBeanService.getDate().indexOf(" ")));
                        registerUserDialog(userBeanService, content, "手术：" + userBeanService.getDiagnosis());
                    } else {
                        saveUser(userBean, onlineUser);
                        UserManager.getInstance().updateUserUploadStatus(userBeans, Global.UploadNetStatus);
                        goEvaluateWithQrCodeDialog();
                    }
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                } else if (code == 401) {
                    getToken(USER_REQ);
                } else {
                    if (reConnected > 0) {
                        syncUser();
                        reConnected--;
                    } else {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            ToastUtils.showShort("数据访问错误");
                        }
                        saveUser(userBean, offlineUser);
                    }
                }
            }
        });

    }

    private void syncNewUser() {
        if (progressDialog == null || !progressDialog.isShowing()) {
            showWaiting("提示", "正在注册...");
        }
        List<UserBean> userBeans = UserManager.getInstance().loadNoNetUploadUser();
        String data = new Gson().toJson(userBeans);
        OkHttpUtils.postJsonAsync(Api.uploadUser, data, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
                ToastUtils.showShort("服务器访问错误");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                goEvaluateDialog();
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                android.util.Log.e("Setting Activity", "--->用户同步结果" + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_USER_RESULT, code));
                if (code == 0) {
                    UserManager.getInstance().updateUserUploadStatus(userBeans, Global.UploadNetStatus);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    goEvaluateWithQrCodeDialog();

                } else if (code == 401) {
                    getToken(NEW_USER);
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        ToastUtils.showShort("数据访问错误");
                    }
                    goEvaluateDialog();
                }
            }
        });

    }

    private void getToken() {
        OkHttpUtils.getAsyncToken(Api.tokenUrl + "?grant_type=client_credentials", true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();

            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("MainActivity", "requestSuccess: " + result);
                TokenBean tokenBean = new Gson().fromJson(result, TokenBean.class);
                if (tokenBean.getCode() == 0) {
                    SPHelper.saveToken(tokenBean.getData().getAccess_token());
                    syncUser();
                }
            }
        });

    }

    private void goEvaluateWithQrCodeDialog() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float density = metrics.density;
        int qrWidth = (int) (300 * density);
        Resources res = getResources();
        Bitmap logoBitmap = BitmapFactory.decodeResource(res, R.mipmap.ic_launcher);
//        String content = Api.qrUrl + userId;
        String content = Api.qrUrl + userId + "/" + codeBean.getMacAddress();
        Bitmap qrBitmap = QrUtil.createQRCodeBitmap(content, qrWidth, qrWidth, "UTF-8", "H", "1", Color.BLACK, Color.WHITE, logoBitmap, 0.15F);
        RxImageDialog dialog = new RxImageDialog(this);
        dialog.setRefreshVisiable(View.GONE);
        dialog.setImage(qrBitmap);
        dialog.setContent("微信扫描同步用户");
        dialog.setCancel("取消");
        dialog.setSure("开始评估");
        Global.USER_MODE = true;
        dialog.setSureListener(v -> {
            if (dialog.isChecked()) {
                dialog.dismiss();
                Bundle bundle = new Bundle();
                bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectUserMode);
                startTargetActivity(bundle, ConnectDeviceActivity.class, true);
            } else {
//                ToastUtils.showShort("请先勾选知情书");
            }
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
            startTargetActivity(MainActivity.class, true);
        });
//        dialog.setRadiobuttonListener(v -> dialog.setRadiobuttonSelect());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void goEvaluateDialog() {
        RxImageDialog dialog = new RxImageDialog(this);
//        dialog.setContent("用户信息未上传到云端，是否开始评估");
        dialog.setCancel("取消");
        dialog.setRefreshVisiable(View.VISIBLE);
        dialog.setSure("开始评估");
        Global.USER_MODE = true;
        dialog.setSureListener(v -> {
            dialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectUserMode);
            startTargetActivity(bundle, ConnectDeviceActivity.class, true);
        });
        dialog.setRefreshListener(v -> {
//            dialog.dismiss();
            if (NetworkUtils.isConnected()) {
                dialog.dismiss();
                showWaiting("提示", "正在同步...");
                syncUser();
            } else {
                ToastUtils.showShort("网络不可用");
//                goEvaluateDialog();
            }
//            dialog.dismiss();
//            startTargetActivity(MainActivity.class, true);
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
            startTargetActivity(MainActivity.class, true);
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void setDisplayView(LinearLayout layout, TextView textView) {
        if (!TextUtils.isEmpty(etAccount.getText().toString())) {
            etName.setText(etAccount.getText());
        }
        layoutStep1.setVisibility(View.GONE);
        layoutStep2.setVisibility(View.GONE);
        layoutStep3.setVisibility(View.GONE);
        tvStep1.setBackgroundResource(R.drawable.round_empty_bg);
        tvStep2.setBackgroundResource(R.drawable.round_empty_bg);
        tvStep3.setBackgroundResource(R.drawable.round_empty_bg);
        layout.setVisibility(View.VISIBLE);
        textView.setBackgroundResource(R.drawable.round_orange_bg);

    }

    private void setDisplayView(RelativeLayout layout, TextView textView) {
        layoutStep1.setVisibility(View.GONE);
        layoutStep2.setVisibility(View.GONE);
        layoutStep3.setVisibility(View.GONE);
        tvStep1.setBackgroundResource(R.drawable.round_empty_bg);
        tvStep2.setBackgroundResource(R.drawable.round_empty_bg);
        tvStep3.setBackgroundResource(R.drawable.round_empty_bg);
        layout.setVisibility(View.VISIBLE);
        textView.setBackgroundResource(R.drawable.round_orange_bg);

    }

    private void addressSelect(int selectStatus) {
        provincesList = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        try {
            String json = ConvertUtils.toString(getAssets().open("city.json"));
            provincesList.addAll(new Gson().fromJson(json, new TypeToken<List<Province>>() {
            }.getType()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (provincesList.size() != 0) {
            builder.setTitle("选择省份");
            List<String> provinces = new ArrayList<>();
            for (Province province : provincesList) {
                provinces.add(province.getAreaName());
            }
            builder.setItems(provinces.toArray(new String[provinces.size()]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Province selectProvince = provincesList.get(which);
                    provinceName = selectProvince.getAreaName();

                    List<City> citiesList = selectProvince.getCities();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("选择城市");
                    ArrayList<String> cities = new ArrayList<>();
                    for (City city : citiesList) {
                        cities.add(city.getAreaName());
                    }
                    builder.setItems(cities.toArray(new String[cities.size()]), (dialog12, which12) -> {
                        City selectCity = citiesList.get(which12);
                        cityName = citiesList.get(which12).getAreaName();

                        List<County> countiesList = selectCity.getThirds();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setTitle("选择区县");
                        ArrayList<String> counties = new ArrayList<>();
                        for (County county : countiesList) {
                            counties.add(county.getAreaName());
                        }
                        builder1.setItems(counties.toArray(new String[counties.size()]), (dialog1, which1) -> {
                            countyName = countiesList.get(which1).getAreaName();
                            city = provinceName + "/" + cityName + "/" + countyName;
                            hospitalAddress = provinceName + "/" + cityName + "/" + countyName;
                            if (selectStatus == SELECT_PERSON_ADDRESS) {
                                tvLocation.setText(city);
                            } else {
                                tvHospitalAddress.setText(hospitalAddress);
                            }
                        });
                        builder1.show();
                    });
                    builder.show();
                }
            });
            if (provinceDialog == null) {
                provinceDialog = builder.show();
            } else {
                if (!provinceDialog.isShowing()) {
                    provinceDialog = builder.show();
                }
            }
        }
    }
    private void operationSelect() {
        operationList = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        try {
            String json = ConvertUtils.toString(getAssets().open("operation_name.json"));
            operationList.addAll(new Gson().fromJson(json, new TypeToken<List<Province>>() {
            }.getType()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (operationList.size() != 0) {
            builder.setTitle("选择部位");
            List<String> provinces = new ArrayList<>();
            for (Province province : operationList) {
                provinces.add(province.getAreaName());
            }
            builder.setItems(provinces.toArray(new String[provinces.size()]), (dialog, which) -> {
                Province selectProvince = operationList.get(which);
                bodyPartName = selectProvince.getAreaName();
                if (!bodyPartName.equals("其他")){
                    List<City> citiesList = selectProvince.getCities();
                    AlertDialog.Builder builder12 = new AlertDialog.Builder(context);
                    builder12.setTitle("选择疾病");
                    ArrayList<String> cities = new ArrayList<>();
                    for (City city : citiesList) {
                        cities.add(city.getAreaName());
                    }
                    builder12.setItems(cities.toArray(new String[cities.size()]), (dialog12, which12) -> {
                        City selectCity = citiesList.get(which12);
                        secondDiseaseName = citiesList.get(which12).getAreaName();

                        List<County> countiesList = selectCity.getThirds();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setTitle("选择治疗方法");
                        ArrayList<String> counties = new ArrayList<>();
                        for (County county : countiesList) {
                            counties.add(county.getAreaName());
                        }
                        builder1.setItems(counties.toArray(new String[counties.size()]), (dialog1, which1) -> {
                            treatmentMethodName = countiesList.get(which1).getAreaName();
                            treatmentMethodId = countiesList.get(which1).getAreaId();
                            tvSelectDiagnosticResult.setText(treatmentMethodName);
                        });
                        builder1.show();
                    });
                    builder12.show();
                }else {
                    treatmentMethodId = selectProvince.getAreaId();
                    RxDialogDiagnosisSelect diagnosisSelect = new RxDialogDiagnosisSelect(context);
                    diagnosisSelect.setSureListener(v -> {
                        String editValue = diagnosisSelect.editText.getText().toString();
                        if (TextUtils.isEmpty(editValue)) {
                            ToastUtils.showShort("请填写详细信息");
                            return;
                        }
                        otherDiagnosis = editValue + "(" + diagnosisSelect.selectValue + ")";
                        tvSelectDiagnosticResult.setText(MessageFormat.format("{0}", otherDiagnosis));
                        diagnosisSelect.dismiss();
                    });
                    diagnosisSelect.show();
                }


            });
            if (provinceDialog == null) {
                provinceDialog = builder.show();
            } else {
                if (!provinceDialog.isShowing()) {
                    provinceDialog = builder.show();
                }
            }
        }
    }

    private void getHospitalByAddress() {
        OkHttpUtils.getAsync(Api.getHospitalByAddress + "?province=" + provinceName + "&city=" + cityName + "&area=" + countyName, true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {

            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Register Activity", "request result: " + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0) {
                    HospitalBean hospitalBean = new Gson().fromJson(result, HospitalBean.class);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("选择医院");
                    ArrayList<String> hospitals = new ArrayList<>();
                    ArrayList<Integer> hospitalIds = new ArrayList<>();
                    for (HospitalBean.DataBean dataBean : hospitalBean.getData()) {
                        hospitals.add(dataBean.getName());
                        hospitalIds.add(dataBean.getId());
                    }
                    builder.setItems(hospitals.toArray(new String[0]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            hospitalName = hospitals.get(which);
                            hospitalId = hospitalIds.get(which);
//                            tvHospitalName.setText(hospitalName);
                            if (NetworkUtils.isConnected()) {
                                getDoctorByHospital(hospitalId);
                            } else {
                                ToastUtils.showShort("网络未连接");
                            }

                        }
                    });
                    builder.show();
                } else if (code == 401) {
                    getToken(HOSPITAL_REQ);
                }

            }
        });
    }

    private void getDoctorByHospital(int hospitalId) {
        OkHttpUtils.getAsync(Api.getDoctorByHospital + "?id=" + hospitalId, true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {

            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Register Activity", "doctor request result: " + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0) {
                    DoctorBean doctorBean = new Gson().fromJson(result, DoctorBean.class);
                    if (doctorList.size() > 0)
                        doctorList.clear();
                    for (DoctorBean.DataBean dataBean : doctorBean.getData()) {
//                        doctorList.add(dataBean.getNickname());
                        linkedHashSet.add(dataBean.getNickname());
                    }
                    tvDoctor.setText("请选择");
                } else if (code == 401) {
                    getToken(DOCTOR_REQ);
                }

            }
        });
    }

    private void getToken(int status) {
        OkHttpUtils.getAsyncToken(Api.tokenUrl + "?grant_type=client_credentials", true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                ToastUtils.showShort(e.getMessage());

            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("MainActivity", "requestSuccess: " + result);
                TokenBean tokenBean = new Gson().fromJson(result, TokenBean.class);
                if (tokenBean.getCode() == 0) {
                    SPHelper.saveToken(tokenBean.getData().getAccess_token());
                    if (status == HOSPITAL_REQ) {
                        getHospitalByAddress();
                    } else if (status == DOCTOR_REQ) {
                        getDoctorByHospital(hospitalId);
                    } else if (status == USER_REQ) {
                        syncUser();
                    } else if (status == NEW_USER) {
                        syncNewUser();
                    }
                } else {
                    ToastUtils.showShort("Token 获取失败");
                }

            }
        });

    }

    private void selectDoctor() {
        if (doctorList.size() > 0)
            doctorList.clear();
        doctorList.addAll(linkedHashSet);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("选择医生");
        builder.setItems(doctorList.toArray(new String[0]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doctor = doctorList.get(which);
                tvDoctor.setText(doctor);
                etDoctor.setText(doctor);
            }
        });
        builder.show();
    }

    private void selectCardType() {
        List<String> list = new ArrayList<>();
        list.add("身份证");
        list.add("病历号");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("选择证件类型");
        builder.setItems(list.toArray(new String[0]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cardType = list.get(which);
                tvCardType.setText(cardType);
                if (which == 0) {
                    tvCard.setText("身  份  证：");
                } else {
                    tvCard.setText("病  例  号：");
                }
            }
        });
        builder.show();
    }

    private void selectedDiseaseDiagnosis() {
        List<String> diseaseList = Arrays.asList(arrayDisease);
        popupSheet = new PopupSheet((Activity) context, tvDiseaseDiagnosis, diseaseList, new PopupSheetCallback() {
            @Override
            public View setupItemView(int position) {
                View itemV = LayoutInflater.from(context).inflate(R.layout.item_surgery_name, null);
                TextView titleTV = itemV.findViewById(R.id.tv_surgery_name);
                titleTV.setText(MessageFormat.format("{0}", diseaseList.get(position)));
                return itemV;
            }

            @Override
            public void itemClicked(ListPopupWindow popupWindow, int position) {
                popupWindow.dismiss();
                diseaseName = diseaseList.get(position);
                tvDiseaseDiagnosis.setText(MessageFormat.format("{0}", diseaseName));
            }
        }, DimensUtil.dp2px(340));
        popupSheet.show();

    }
    private void selectedEducationLevel() {
        List<String> educationLevelList = Arrays.asList(arrayEducationLevel);
        popupSheet = new PopupSheet((Activity) context, tvEducationLevel, educationLevelList, new PopupSheetCallback() {
            @Override
            public View setupItemView(int position) {
                View itemV = LayoutInflater.from(context).inflate(R.layout.item_surgery_name, null);
                TextView titleTV = itemV.findViewById(R.id.tv_surgery_name);
                titleTV.setText(MessageFormat.format("{0}", educationLevelList.get(position)));
                return itemV;
            }

            @Override
            public void itemClicked(ListPopupWindow popupWindow, int position) {
                popupWindow.dismiss();
                educationLevel = educationLevelList.get(position);
                tvEducationLevel.setText(MessageFormat.format("{0}", educationLevel));
            }
        }, DimensUtil.dp2px(340));
        popupSheet.show();

    }
    private void selectedDoctorTeam() {
        popupSheet = new PopupSheet((Activity) context, tvDoctorTeam, teamList, new PopupSheetCallback() {
            @Override
            public View setupItemView(int position) {
                View itemV = LayoutInflater.from(context).inflate(R.layout.item_surgery_name, null);
                TextView titleTV = itemV.findViewById(R.id.tv_surgery_name);
                titleTV.setText(MessageFormat.format("{0}", teamList.get(position)));
                return itemV;
            }

            @Override
            public void itemClicked(ListPopupWindow popupWindow, int position) {
                popupWindow.dismiss();
                teamName = teamList.get(position);
                teamId = teamIdList.get(position);
                tvDoctorTeam.setText(MessageFormat.format("{0}", teamName));
            }
        }, DimensUtil.dp2px(340));
        popupSheet.show();

    }
    private void selectedHeight() {
        popupSheet = new PopupSheet((Activity) context, tvSelectHeightResult, heightList, new PopupSheetCallback() {
            @Override
            public View setupItemView(int position) {
                View itemV = LayoutInflater.from(context).inflate(R.layout.item_surgery_name, null);
                TextView titleTV = itemV.findViewById(R.id.tv_surgery_name);
                titleTV.setText(MessageFormat.format("{0}", heightList.get(position)));
                return itemV;
            }

            @Override
            public void itemClicked(ListPopupWindow popupWindow, int position) {
                popupWindow.dismiss();
                height = heightList.get(position)+"";
                tvSelectHeightResult.setText(MessageFormat.format("{0}", height));
            }
        }, DimensUtil.dp2px(340));
        popupSheet.show();
//        popupSheet.setDisplayPosition(heightList.size()/2);

    }

    private void selectedSurgeryName() {
        List<String> surgeryNameList = Arrays.asList(array);
        popupSheet = new PopupSheet((Activity) context, rlDiagnostic, surgeryNameList, new PopupSheetCallback() {
            @Override
            public View setupItemView(int position) {
                View itemV = LayoutInflater.from(context).inflate(R.layout.item_surgery_name, null);
                TextView titleTV = itemV.findViewById(R.id.tv_surgery_name);
                titleTV.setText(MessageFormat.format("{0}", surgeryNameList.get(position)));
                return itemV;
            }

            @Override
            public void itemClicked(ListPopupWindow popupWindow, int position) {
                popupWindow.dismiss();
                selectPosition = position;
                String surgeryName = surgeryNameList.get(position);
                if (position == surgeryNameList.size() - 1) {
                    RxDialogDiagnosisSelect diagnosisSelect = new RxDialogDiagnosisSelect(context);
                    diagnosisSelect.setSureListener(v -> {
                        String editValue = diagnosisSelect.editText.getText().toString();
                        if (TextUtils.isEmpty(editValue)) {
                            ToastUtils.showShort("请填写详细信息");
                            return;
                        }
                        otherDiagnosis = editValue + "(" + diagnosisSelect.selectValue + ")";
                        tvSelectDiagnosticResult.setText(MessageFormat.format("{0}", otherDiagnosis));
                        diagnosisSelect.dismiss();
                    });
                    diagnosisSelect.show();
                } else {
                    tvSelectDiagnosticResult.setText(MessageFormat.format("{0}", surgeryName));
                }
            }
        }, DimensUtil.dp2px(340));
        popupSheet.show();
    }

    private void initDataTime() {
        Calendar c = Calendar.getInstance();
        mDatePickerDialog = new MyDatePickerDialog(context,
                // 绑定监听器
                (view, year, monthOfYear, dayOfMonth) -> {
                    int month = monthOfYear + 1;
                    DateTime a = new DateTime(year, month, dayOfMonth, 0, 0);
//                    DateTime curDate = DateTime.now();
//                    if (a.isAfter(curDate)) {
//                        year = curDate.getYear();
//                        month = curDate.getMonthOfYear();
//                        dayOfMonth = curDate.getDayOfMonth();
//                    }
//                    userBean.setDate(new DateTime(year, month, dayOfMonth, DateTime.now().getHourOfDay(), DateTime.now().getMinuteOfHour()).toString("yyyy-MM-dd HH:mm:ss"));
                    userBean.setDate(new DateTime(year, month, dayOfMonth, 0, 0).toString("yyyy-MM-dd HH:mm:ss"));
                    dateOfSurgery = year + "-" + month + "-" + dayOfMonth;
                    tvDate.setText(dateOfSurgery);
                }
                // 设置初始日期
                , c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
    }

}
