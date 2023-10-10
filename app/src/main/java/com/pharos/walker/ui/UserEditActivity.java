package com.pharos.walker.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.ListPopupWindow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pharos.walker.MainActivity;
import com.pharos.walker.beans.TokenBean;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.R;
import com.pharos.walker.adapter.AgeArrayAdapter;
import com.pharos.walker.constants.Api;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.customview.MyDatePickerDialog;
import com.pharos.walker.customview.popupdialog.PopupSheet;
import com.pharos.walker.customview.popupdialog.PopupSheetCallback;
import com.pharos.walker.customview.rxdialog.RxDialogDiagnosisSelect;
import com.pharos.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharos.walker.database.UserManager;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.DimensUtil;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.NetworkUtils;
import com.pharos.walker.utils.OkHttpUtils;
import com.pharos.walker.utils.RegexUtils;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
 * Created by zhanglun on 2021/4/25
 * Describe:
 */
public class UserEditActivity extends BaseActivity {
    @BindView(R.id.radioButton)
    RadioButton txtMan;
    @BindView(R.id.radioButton2)
    RadioButton txtWoman;
    @BindView(R.id.txtSex)
    RadioGroup txtSex;
    @BindView(R.id.txtUserName)
    EditText txtUserName;
    @BindView(R.id.txtDiag)
    EditText txtDiag;
    @BindView(R.id.txtPhone)
    EditText txtPhone;
    @BindView(R.id.textView27)
    TextView textView27;
    @BindView(R.id.tv_doctor)
    EditText tvDoctor;
    @BindView(R.id.cmbAge)
    Spinner cmbAge;
    @BindView(R.id.txtCaseHistoryNO)
    EditText txtCaseHistoryNO;
    @BindView(R.id.tv_weight)
    EditText tvWeight;
    @BindView(R.id.txtUserDate)
    TextView txtUserDate;
    @BindView(R.id.textAge)
    TextView textAge;
    @BindView(R.id.sp_select_diagnostic_result)
    Spinner spinner;
    @BindView(R.id.tv_select_diagnostic_result)
    TextView tvSelectDiagnosticResult;
    @BindView(R.id.btnUserEditReturn)
    Button btnUserEditReturn;
    @BindView(R.id.btnUserEditSave)
    Button btnUserEditSave;
    private MyDatePickerDialog mDatePickerDialog;
    private List<Integer> ageList = null;
    private int mode = 0;//0 新增用户 1更新 2查看
    private UserBean userBean;
    private UserManager mUserManager;
    private String sickType;
    private int selectPosition = -1;
    private boolean isFirst = true;
    private long userId;
    private String[] array;
    private List<Province> operationList;
    private AlertDialog provinceDialog;
    private String bodyPartName;//部位
    private String secondDiseaseNameSecond;//疾病名称
    private String treatmentMethodName;//治疗方式
    private String treatmentMethodId;//治疗方式Id
    private Context context;
    private String otherDiagnosis;
    private int EDIT_USER = 0;
    private PopupSheet popupSheet;
    private int age;
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        context = this;
        initData();
        initView();
        initDataTime();
//        initAgeAdapter();

    }

    private void initView() {
        if (mode == 1 || mode == 2) {
            txtUserName.setText(userBean.getName());
            txtDiag.setText(userBean.getDiagnosis());
            tvSelectDiagnosticResult.setText(userBean.getDiagnosis());
            txtPhone.setText(userBean.getTelephone());
            if (userBean.getSex() == 1) {
                txtMan.setChecked(true);
                txtWoman.setChecked(false);
            } else {
                txtMan.setChecked(false);
                txtWoman.setChecked(true);
            }
            age = userBean.getAge();
            textAge.setText(String.valueOf(age));
            tvDoctor.setText(userBean.getDoctor());
            txtCaseHistoryNO.setText(userBean.getCaseHistoryNo());
            txtUserDate.setText(DateFormatUtil.getString(userBean.getDate()));
            tvWeight.setText(userBean.getWeight());
            treatmentMethodId = userBean.getTreatmentMethodId();
        }
        if (mode == 2) {
            txtUserName.setFocusable(false);
            txtCaseHistoryNO.setFocusable(false);
            cmbAge.setEnabled(false);
            txtUserDate.setEnabled(false);
            txtSex.setFocusable(false);
            txtMan.setClickable(false);
            txtWoman.setClickable(false);
            tvDoctor.setFocusable(false);
            txtPhone.setFocusable(false);
            txtDiag.setFocusable(false);
            spinner.setFocusable(false);
            spinner.setClickable(false);
            spinner.setEnabled(false);
            btnUserEditSave.setVisibility(View.GONE);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position;
                TextView textView = null;
                if (view instanceof TextView && position == 0) {
                    textView = ((TextView) view);
                    textView.setTextColor(getColor(R.color.white_60));
                }else if (view instanceof TextView){
                    textView = ((TextView) view);
                    textView.setTextColor(getColor(R.color.white));
                }
                if (selectPosition == parent.getCount() -1){
                    txtDiag.setVisibility(View.VISIBLE);
                }else {
                    txtDiag.setVisibility(View.INVISIBLE);
                }
                if (selectPosition == parent.getCount() -1 && !isFirst){
                    RxDialogDiagnosisSelect diagnosisSelect = new RxDialogDiagnosisSelect(UserEditActivity.this);
                    diagnosisSelect.setSureListener(v -> {
                        String editValue = diagnosisSelect.editText.getText().toString();
                        if (TextUtils.isEmpty(editValue)){
                            ToastUtils.showShort("请填写详细信息");
                            return;
                        }
                        String otherDiagnosis = editValue + "(" + diagnosisSelect.selectValue + ")";
                        txtDiag.setText(otherDiagnosis);
                        diagnosisSelect.dismiss();
                    });
                    diagnosisSelect.show();
                }
                isFirst = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    Class<?> clazz = AdapterView.class;
                    Field field = clazz.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);
                    field.setInt(spinner,Integer.MIN_VALUE);
                } catch(Exception e){
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    private void initData() {
        mode = getIntent().getIntExtra("Mode", 0);
        userId = getIntent().getLongExtra("userId", 0);
        mUserManager = UserManager.getInstance();

        if (mode == 0) {
            userBean = new UserBean();
        } else {
            userBean = UserManager.getInstance().loadByUserId(userId);
            sickType = userBean.getDiagnosis();
            selectPosition = MyUtil.getDiagnosticNum(sickType);
            Resources res = getResources();
            if (SPHelper.getOperationSwitch() || MyUtil.getDiagnosticNum(userBean.getDiagnosis()) == 10){
                array = res.getStringArray(R.array.diagnostic_result_list_1);
            }else {
                array = res.getStringArray(R.array.diagnostic_result_list);
            }
            ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.simple_spinner_list_item,array);
            spinner.setAdapter(arrayAdapter);
//            if (selectPosition != array.length -1){
//                spinner.setSelection(selectPosition);
//            }else {
//                txtDiag.setVisibility(View.VISIBLE);
//            }
            if (!SPHelper.getOperationSwitch() && selectPosition > 10){
                spinner.setSelection(selectPosition-1);
            }else {
                spinner.setSelection(selectPosition);
            }
        }
        if (SPHelper.getWeightLimitSwitch()){
            Global.minWeight = 30;
            Global.maxWeight = 130;
        }else {
            Global.minWeight = 35;
            Global.maxWeight = 100;
        }
        if (ageList == null) {
            ageList = new ArrayList<>();
            for (int i = 10; i < 100; i++) {
                ageList.add(i);
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
                        secondDiseaseNameSecond = citiesList.get(which12).getAreaName();

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
    private void initAgeAdapter() {
        ArrayAdapter<Integer> ageAdapter = new AgeArrayAdapter(UserEditActivity.this, ageList);
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        cmbAge.setAdapter(ageAdapter);

        cmbAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(getColor(R.color.white));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        if (mode == 1 || mode == 2) {
//            cmbAge.setSelection(userBean.getAge() - 10);
//        } else {
//            cmbAge.setSelection(40);
//        }

    }
    private void initDataTime() {
        Calendar c = Calendar.getInstance();
        mDatePickerDialog = new MyDatePickerDialog(UserEditActivity.this,
                // 绑定监听器
                (view, year, monthOfYear, dayOfMonth) -> {
                    int month = monthOfYear + 1;
                    DateTime a = new DateTime(year, month, dayOfMonth, 0, 0);
                    DateTime curDate = DateTime.now();
                    if (a.isAfter(curDate)) {
                        year = curDate.getYear();
                        month = curDate.getMonthOfYear();
                        dayOfMonth = curDate.getDayOfMonth();
                    }
                    userBean.setDate(new DateTime(year, month, dayOfMonth, 0, 0).toString("yyyy-MM-dd HH:mm:ss"));
                    txtUserDate.setText(year + "-" + month + "-" + dayOfMonth);
                }
                // 设置初始日期
                , c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
    }



    @Override
    protected int getLayoutResId() {
        return R.layout.activity_user_edit;
    }

    @OnClick({R.id.btnUserEditReturn, R.id.btnUserEditSave, R.id.txtUserDate,R.id.tv_select_diagnostic_result,R.id.textAge})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnUserEditReturn:
                finish();
                break;
            case R.id.btnUserEditSave:
                saveUser();
                break;
            case  R.id.txtUserDate:
                if (mDatePickerDialog != null) {
                    mDatePickerDialog.show();
                }
                break;
            case R.id.tv_select_diagnostic_result:
                if (mode == 1 || mode == 0){
                    operationSelect();
                }

                break;
            case R.id.textAge:
                selectedHeight();
                break;
        }
    }
    private void selectedHeight() {
        popupSheet = new PopupSheet((Activity) context, textAge, ageList, new PopupSheetCallback() {
            @Override
            public View setupItemView(int position) {
                View itemV = LayoutInflater.from(context).inflate(R.layout.item_surgery_name, null);
                TextView titleTV = itemV.findViewById(R.id.tv_surgery_name);
                titleTV.setText(MessageFormat.format("{0}", ageList.get(position)));
                return itemV;
            }

            @Override
            public void itemClicked(ListPopupWindow popupWindow, int position) {
                popupWindow.dismiss();
                age = ageList.get(position);
                textAge.setText(MessageFormat.format("{0}", age));
            }
        }, DimensUtil.dp2px(340));
        popupSheet.show();
//        popupSheet.setDisplayPosition(heightList.size()/2);

    }
    private void saveUser() {
        boolean valid = ValidData();
        if (valid) {
            String caseHistoryNum = txtCaseHistoryNO.getText().toString();
            userBean.setName(txtUserName.getText().toString());
            userBean.setCaseHistoryNo(caseHistoryNum);
            userBean.setAge(age);
//            userBean.setAge(Integer.parseInt(cmbAge.getSelectedItem().toString()));
            String diag = txtDiag.getText().toString();
            userBean.setWeight(tvWeight.getText().toString());
//            userBean.setRemark(txtDiag.getText().toString());
            if (txtSex.getCheckedRadioButtonId() == txtWoman.getId()) {
                userBean.setSex(0);
            } else {
                userBean.setSex(1);
            }
            userBean.setDoctor(tvDoctor.getText().toString());
            userBean.setTelephone(txtPhone.getText().toString());
//            if (selectPosition == array.length -1 && !TextUtils.isEmpty(diag)){
//                userBean.setDiagnosis(diag);
//            }else {
//                userBean.setDiagnosis(array[selectPosition]);
//            }
            userBean.setTreatmentMethodId(treatmentMethodId);
            userBean.setDiagnosis(tvSelectDiagnosticResult.getText().toString());
            if (mUserManager.isUniqueValue(caseHistoryNum,mode)){
                try {
                    mUserManager.insert(userBean,mode);
                }catch (SQLiteConstraintException e){
                    if (e.getMessage().contains("USER.CASE_HISTORY_NO")){
                        ToastUtils.showShort("病历号重复了，请修改");
                    }
                    return;
                }
            }else {
                ToastUtils.showShort("病历号重复了，请修改");
                return;
            }
            if (mode == 1 && NetworkUtils.isConnected()){
                syncUser();
            }
            if(mode == 0){
                goEvaluateDialog();
            }else {
                finish();
            }

        }
    }

    private void syncUser() {
        if (progressDialog == null || !progressDialog.isShowing()) {
            showWaiting("提示", "正在保存...");
        }
        List<UserBean> userBeans = new ArrayList<>();
        userBeans.add(userBean);
        String data = new Gson().toJson(userBeans);
        OkHttpUtils.postJsonAsync(Api.uploadUser, data, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
                ToastUtils.showShort("服务器访问错误");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
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
                    ToastUtils.showShort("保存成功！");
                    finish();
                } else if (code == 401) {
                    getToken(EDIT_USER);
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        ToastUtils.showShort("数据访问错误");
                    }
                }
            }
        });

    }
    private void goEvaluateDialog(){
        RxDialogSureCancel dialog = new RxDialogSureCancel(this);
        dialog.setContent("是否开始评估");
        dialog.setCancel("取消");
        dialog.setSure("开始评估");
        dialog.setSureListener(v -> {
            dialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE,Global.ConnectUserMode);
            startTargetActivity(bundle,ConnectDeviceActivity.class,true);
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
            startTargetActivity(MainActivity.class,true);
        });
        dialog.show();
    }
    private boolean ValidData() {
        if (txtUserName.getText().toString().trim().isEmpty()) {
            Toast.makeText(UserEditActivity.this, "请填写姓名!", Toast.LENGTH_SHORT).show();
            return false;
        }
//        if (!RegexUtils.isName(txtUserName.getText().toString())) {
//            Toast.makeText(UserEditActivity.this, "姓名不正确!", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if (TextUtils.isEmpty(txtCaseHistoryNO.getText().toString().trim())) {
            Toast.makeText(UserEditActivity.this, "请填写病历号!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!(txtPhone.getText().toString().startsWith("1") && txtPhone.getText().toString().length() == 11) && !TextUtils.isEmpty(txtPhone.getText().toString().trim())) {
            Toast.makeText(UserEditActivity.this, "手机号不正确!", Toast.LENGTH_SHORT).show();
            return false;
        }
        String weight = tvWeight.getText().toString().trim();
        if (TextUtils.isEmpty(tvWeight.getText())) {
            Toast.makeText(UserEditActivity.this, "请填写体重!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Integer.parseInt(weight) < Global.minWeight || Integer.parseInt(weight) > Global.maxWeight) {
            ToastUtils.showShort(MessageFormat.format("体重必须在{0}kg到{1}kg之间",Global.minWeight,Global.maxWeight));
            return false;
        }
        if (txtSex.getCheckedRadioButtonId() != txtMan.getId() && txtSex.getCheckedRadioButtonId() != txtWoman.getId()) {
            Toast.makeText(UserEditActivity.this, "请选择性别!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectPosition <= 0) {
            Toast.makeText(UserEditActivity.this, "请填写诊断结果!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(txtUserDate.getText().toString().trim())) {
            Toast.makeText(UserEditActivity.this, "请填写手术日期!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void getToken(int flag){
        OkHttpUtils.getAsyncToken(Api.tokenUrl + "?grant_type=client_credentials" , true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();

            }
            @Override
            public void requestSuccess(String result) throws Exception {
                com.tencent.mars.xlog.Log.e("MainActivity", "requestSuccess: " + result);
                TokenBean tokenBean = new Gson().fromJson(result,TokenBean.class);
                if (tokenBean.getCode() == 0){
                    SPHelper.saveToken(tokenBean.getData().getAccess_token());
                    if (flag == EDIT_USER){
                        syncUser();
                    }
                }
            }
        });
    }
}
