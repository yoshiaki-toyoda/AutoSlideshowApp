package jp.techacademy.yoshiaki.autoslideshowapp

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.PermissionChecker.checkSelfPermission
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.NullPointerException
import java.util.*
import kotlin.contracts.contract

open class MainActivity : AppCompatActivity() {
    //permission様変数
    private val PERMISSIONS_REQUEST_CODE = 100
    private var mTimer: Timer? = null
    private var mHandler = Handler()
    private var mTimerSec = 0.0 // タイマー用の時間のための変数

    var count:Int=0 //画像URL格納辞書のキー　初期値
    var maxcount:Int=0//辞書格納件数
    var timerstate:Int=0//タイマー停止/再生判断用変数　0再生　1停止

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissions(count)
        start_button.text="再生" //スライドショー初回文字設定

        //戻るボタン押下
            prebutton.setOnClickListener {
                count=Button(count).prebutton(count,maxcount)
                getContentsInfo(count)
                //imageUri2= Getimage().getContentsInfo(count,maxcount)
               // imageView.setImageURI(imageUri2)//画像表示
            }

        //進むボタン押下時
         nextbutton.setOnClickListener {
             count=Button(count).nextbutton(count,maxcount)
             getContentsInfo(count)
            // imageUri2= Getimage().getContentsInfo(count,maxcount)
             //imageView.setImageURI(imageUri2)//画像表示
         }

        //スライドショー
        start_button.setOnClickListener {
            if (timerstate==0) {
                if (mTimer == null) {
                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            mTimerSec += 0.1
                            mHandler.post {
                                count = Button(count).nextbutton(count, maxcount)
                                getContentsInfo(count)
                                timerstate=1
                                start_button.text="停止"//ボタン文字変更
                                prebutton.isClickable = false//戻るボタン操作無効
                                nextbutton.isClickable = false//次へボタン操作無効
                            }
                        }
                    }, 100, 2000) // 最初に始動させるまで 100ミリ秒、ループの間隔を 100ミリ秒 に設定
                }
            }else if (timerstate==1){
                if (mTimer != null){
                    mTimer!!.cancel()
                    mTimer = null
                    timerstate=0
                    start_button.text="再生"//ボタン文字変更
                    prebutton.isClickable = true//戻るボタン操作無効
                    nextbutton.isClickable = true//次へボタン操作無効
                }
            }
        }
    }


    //permisson用関数 permissions
    fun permissions(count: Int){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo(count)
                //imageUri2= Getimage().getContentsInfo(count,maxcount)
                //imageView.setImageURI(imageUri2)//画像表示
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo(count)
            //imageUri2= Getimage().getContentsInfo(count,maxcount)
            //imageView.setImageURI(imageUri2)//画像表示
        }
    }

    //permission未許可時の許可対応
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            when (requestCode) {
                PERMISSIONS_REQUEST_CODE ->
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        getContentsInfo(count)
                    }
            }
    }

    //画像取得
    private fun getContentsInfo(count:Int) {
        // 画像の情報を取得する
        this.count=count//辞書呼び出しキー用変数
        var count2:Int=0//辞書格納用キー変数


        val resolver = contentResolver
        val urlmap = mutableMapOf<Int,Uri>()
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目(null = 全項目)
            null, // フィルタ条件(null = フィルタなし)
            null, // フィルタ用パラメータ
            null // ソート (null ソートなし)
        )
        //画像URL取得
        if (cursor!!.moveToFirst()) {
            do {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                //獲得したurlを辞書に格納する
                urlmap.put(count2,imageUri)
                //辞書キーに1を足す
                count2=count2+1
            } while (cursor.moveToNext())

            //キーが辞書の数を超えた際に辞書内の最大値に戻す
                maxcount=count2-1
            //辞書からUrl呼び出し
                var imageUri2 = urlmap[count]
                 imageView.setImageURI(imageUri2)//画像表示
        }
        cursor.close()
    }
}

