package com.map202306.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.net.Uri;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.util.Log;

public class Sos extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST_CODE = 100;
    private Button importButton;
    private EditText editTextTitle;
    private EditText editTextContent;
    private Button buttonSend;
    private DatabaseReference databaseRef;
    private TextView importImgTextView;
    private StorageReference storageRef;

    private static final String TAG = "LogUtil";
    private static final String LOG_DIR = "MyAppLogs";
    private static final String LOG_FILE_NAME = "log.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_sos);
        LogUtil.writeLog("오류 메시지", this);
        databaseRef = FirebaseDatabase.getInstance().getReference("reports");
        storageRef = FirebaseStorage.getInstance().getReference();
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        buttonSend = findViewById(R.id.send_Button);
        importButton = findViewById(R.id.import_button);
        importImgTextView = findViewById(R.id.import_img);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString().trim();
                String content = editTextContent.getText().toString().trim();
                String reportId = databaseRef.push().getKey();
                if (title.isEmpty()) {
                    Toast.makeText(Sos.this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (content.isEmpty()) {
                    Toast.makeText(Sos.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Report report = new Report(reportId, title, content);
                databaseRef.child(reportId).setValue(report);
                Toast.makeText(Sos.this, "문제가 신고되었습니다.", Toast.LENGTH_SHORT).show();

                Uri logFileUri = getLogFileUri();
                if (logFileUri != null) {
                    uploadLogFile(logFileUri, reportId);
                } else {
                    Toast.makeText(Sos.this, "로그 파일을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(Sos.this, "문제가 신고되었습니다.", Toast.LENGTH_SHORT).show();
                finish();

                finish();
            }
        });
    }

    private void uploadLogFile(Uri logFileUri, String reportId) {
        if (logFileUri != null) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = timestamp + "log.txt";
            StorageReference fileRef = storageRef.child("reports/" + fileName);

            UploadTask uploadTask = fileRef.putFile(logFileUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            String downloadUrl = downloadUri.toString();
                            databaseRef.child(reportId).child("logFileUrl").setValue(downloadUrl);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Sos.this, "로그 파일 다운로드 URL 가져오기 실패.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof StorageException) {
                        StorageException storageException = (StorageException) e;
                        int errorCode = storageException.getErrorCode();
                        if (errorCode == StorageException.ERROR_QUOTA_EXCEEDED) {
                            Toast.makeText(Sos.this, "Firebase Storage 일일 할당량을 초과했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    Toast.makeText(Sos.this, "로그 파일 업로드 실패.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // 모든 파일 유형을 선택할 수 있도록 설정
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri fileUri = data.getData();
                String fileName = getFileName(fileUri);
                importImgTextView.setText(fileName);       //업로드 할 파일의 이름으로 텍스트 뷰 내용 변경
                Uri logFileUri = getLogFileUri(); // 로그 파일의 URI
                uploadFiles(new Uri[]{fileUri, logFileUri}); // 파일 URI 배열을 전달하여 업로드
            }
        }
    }
    private void uploadFiles(Uri[] fileUris) {
        for (Uri fileUri : fileUris) {
            if (fileUri != null) {
                String folderName = generateFolderName();
                // 파일 이름 변경
                String originalFileName = getFileName(fileUri);
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss  ", Locale.getDefault()).format(new Date());
                StorageReference folderRef = storageRef.child("reports/" + folderName);
                StorageReference fileRef = folderRef.child(fileUri.getLastPathSegment());

                UploadTask uploadTask = fileRef.putFile(fileUri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // 파일 업로드 성공
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                LogUtil.writeLog("", Sos.this); // 로그를 파일에 기록
                                String downloadUrl = downloadUri.toString();
                                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                                String fileName = timestamp + "log.txt";
                                StorageReference fileRef = storageRef.child("reports/" + fileName);
                                // 파일 URL을 데이터베이스에 저장하
                                String reportId = databaseRef.push().getKey();
                                databaseRef.child(reportId).child("fileUrl").setValue(downloadUrl);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 파일 다운로드 URL을 가져오는 중에 오류가 발생한 경우
                                Toast.makeText(Sos.this, "파일 다운로드 URL 가져오기 실패.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 파일 업로드 중에 오류가 발생한 경우
                        if (e instanceof StorageException) {
                            StorageException storageException = (StorageException) e;
                            int errorCode = storageException.getErrorCode();
                            if (errorCode == StorageException.ERROR_QUOTA_EXCEEDED) {
                                Toast.makeText(Sos.this, "Firebase Storage 일일 할당량을 초과했습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        Toast.makeText(Sos.this, "파일 업로드 실패.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }
    }

    //파이어베이스 내부 저장소 폴더 추가 생성
    private String generateFolderName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }


    private String getFileName(Uri fileUri) {
        String fileName = null;
        if (fileUri != null) {
            fileName = fileUri.getLastPathSegment();
        }
        return fileName;
    }

    private Uri getLogFileUri() {
        File logFile = LogUtil.getLogFile(this);
        if (logFile != null) {
            return Uri.fromFile(logFile);
        }
        return null;
    }
    //로그 퍼일의 생성 부분
    public static class LogUtil {
        private static final String TAG = "LogUtil";
        private static final String LOG_DIR = "logs";
        private static final String LOG_FILE_NAME = "log.txt";
        public static void writeLog(String message, Context context) {
            File logFile = getLogFile(context);

            if (logFile != null) {
                try {
                    FileWriter fileWriter = new FileWriter(logFile, true);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String logMessage = dateFormat.format(new Date()) + " - " + message + "\n";
                    fileWriter.append(logMessage);
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error writing log file: ", e);
                }
            } else {
                Log.e(TAG, "Log file is null");
            }
        }

        private static File getLogFile(Context context) {
            File logDir = new File(context.getExternalFilesDir(null), LOG_DIR);
            if (!logDir.exists()) {
                if (!logDir.mkdirs()) {
                    Log.e(TAG, "Error creating log directory");
                    return null;
                }
            }

            File logFile = new File(logDir, LOG_FILE_NAME);

            if (!logFile.exists()) {
                try {
                    if (!logFile.createNewFile()) {
                        Log.e(TAG, "Error creating log file");
                        return null;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error creating log file: ", e);
                    return null;
                }
            }

            return logFile;
        }
    }
}
