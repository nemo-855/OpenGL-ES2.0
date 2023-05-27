package com.nemo.opengl;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class HelloQuad extends Activity implements GLSurfaceView.Renderer {
    // 頂点シェーダのプログラム
    private static final String VSHADER_SOURCE =
            "attribute vec4 a_Position;\n" +
                    "void main() {\n" +
                    "  gl_Position = a_Position;\n" +
                    "}\n";

    // フラグメントシェーダのプログラム
    private static final String FSHADER_SOURCE =
            "void main() {\n" +
                    "  gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);\n" +
                    "}\n";

    // メンバー変数
    private int mNumVertices; // 描画する頂点数

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // OpenGL ES 2.0が使用できるように初期化する
        GLSurfaceView glSurfaceView = Utils.initGLES20(this, this);
        setContentView(glSurfaceView);// GLSurfaceViewをこのアプリケーションの画面として使用する
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // シェーダを初期化する
        int program = Utils.initShaders(VSHADER_SOURCE, FSHADER_SOURCE);

        // 頂点座標を設定する
        mNumVertices = initVertexBuffers(program);

        // 画面をクリアする色を設定する
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 表示領域を設定する
        int size = width <= height ? width : height;
        GLES20.glViewport((width - size) / 2, (height - size) / 2, size, size);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);  		            // 描画領域をクリアする
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mNumVertices); // 四角形を描画する
    }

    private int initVertexBuffers(int program) {
        // 頂点座標
        FloatBuffer vertices = Utils.makeFloatBuffer(new float[]{
                -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f,
        });
        int n = 4; // 頂点数
        final int FSIZE = Float.SIZE / Byte.SIZE; // floatのバイト数

        // バッファオブジェクトを作成する
        int[] vertexBuffer = new int[1];
        GLES20.glGenBuffers(1, vertexBuffer, 0);

        // バッファオブジェクトをターゲットにバインドする
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBuffer[0]);
        // バッファオブジェクトにデータを書き込む
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, FSIZE * vertices.limit(), vertices, GLES20.GL_STATIC_DRAW);

        // attribute変数の格納場所を取得する
        int a_Position = GLES20.glGetAttribLocation(program, "a_Position");
        if (a_Position == -1) {
            throw new RuntimeException("a_Positionの格納場所の取得に失敗");
        }
        // a_Position変数にバッファオブジェクトを割り当てる
        GLES20.glVertexAttribPointer(a_Position, 2, GLES20.GL_FLOAT, false, 0, 0);

        // a_Position変数でのバッファオブジェクトの割り当てを有効にする
        GLES20.glEnableVertexAttribArray(a_Position);

        return n;
    }
}