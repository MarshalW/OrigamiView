package com.example.origami;

import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-3-10
 * Time: 下午2:07
 * To change this template use File | Settings | File Templates.
 */
public class OrigamiRenderer implements GLSurfaceView.Renderer {

    private float[] projectionMatrix = new float[16];

    private float headHeight, contentHeight;

    private int[] headTextureIds;

    private OrigamiView origamiView;

    private List<float[]> headerVertexes;

    private float ratio;

    private FloatBuffer headVertexBuffer, textureCoordBuffer;

    private Shader headShader;

    private float factor;

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0f, 0f, 0f, 0.0f);

        this.headerVertexes = new ArrayList<float[]>();
        this.initShader();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        ratio = width / (float) height;

        GLES20.glViewport(0, 0, width, height);
        Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, -10f, 10f);

        headShader.useProgram();
        GLES20.glUniformMatrix4fv(headShader.getHandle("uProjectionM"), 1, false, projectionMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if (this.headVertexBuffer != null) {
            this.draw();
        }
    }

    public void chooseItem(OrigamiView view, int oldChooseIndex) {
//        view.getTargetLayout().setVisibility(View.INVISIBLE);

        this.origamiView = view;
        this.setDrawData();
        view.getOrigamiAnimationView().requestRender();
    }

    private void initShader() {
        String vertexShader =
                "        uniform mat4 uProjectionM;\n" +
                        "attribute vec3 aPosition;\n" +
                        "attribute vec4 aColor;\n" +
                        "attribute vec2 aTextureCoord;\n" +
                        "varying vec4 vColor;\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "void main() {\n" +
                        "  gl_Position = uProjectionM * vec4(aPosition, 1.0);\n" +
                        "  vColor = aColor;\n" +
                        "  vTextureCoord = aTextureCoord;\n" +
                        "}\n";
        String fragmentShader =
                "        precision mediump float;\n" +
                        "varying vec4 vColor;\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "uniform sampler2D sTexture;\n" +
                        "void main() {\n" +
                        "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
//                        "  gl_FragColor.rgb *= vColor.rgb;\n" +
//                        "  gl_FragColor = mix(vColor, gl_FragColor, vColor.a);\n" +
//                        "  gl_FragColor.a = 1.0;\n" +
                        "}\n";

        headShader = new Shader();
        headShader.setProgram(vertexShader, fragmentShader);

    }

    private void setHeadVertexesData() {
        //实现最简单的倒排序，给内容留出空间
        List<float[]> _vertexes = new ArrayList<float[]>();
        float currentTop = -1;
        for (int i = 0; i < origamiView.getOrigamiItems().size(); i++) {
            Log.d("origami","choose index: "+origamiView.getChooseIndex());
            if (origamiView.getChooseIndex() == origamiView.getOrigamiItems().size() - 1 - i) {
                currentTop += contentHeight;
                Log.d("origami","current top: "+currentTop);
            }
            currentTop += headHeight;

            float[] vertexes = new float[]{
                    -ratio, currentTop, 0,
                    -ratio, currentTop - headHeight, 0,
                    ratio, currentTop, 0,
                    ratio, currentTop - headHeight, 0
            };
            _vertexes.add(vertexes);
        }
        Collections.reverse(_vertexes);

        for(float[] v:_vertexes){
            headVertexBuffer.put(v);
        }
    }

    /**
     * 设置数据，用于后续的绘制
     */
    private void setDrawData() {
        //设置头和内容条目的高度
        headHeight = 2 * (origamiView.getHeadHeight() / (float) origamiView.getHeight());
        contentHeight = 2 * (origamiView.getContentHeight() / (float) origamiView.getHeight());

        if (headVertexBuffer == null) {
            //条目标题顶点数，有重合的
            int vertexCount = origamiView.getOrigamiItems().size() * 4;
            headVertexBuffer = ByteBuffer.allocateDirect(vertexCount * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

            //设置纹理坐标
            textureCoordBuffer = ByteBuffer.allocateDirect(4 * 2 * 4 * origamiView.getOrigamiItems().size())
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();

            for (int i = 0; i < origamiView.getOrigamiItems().size(); i++) {
                textureCoordBuffer.put(new float[]{
                        0, 0,
                        0, 1,
                        1, 0,
                        1, 1
                });
            }
        } else {
            headVertexBuffer.clear();
        }

        this.setHeadVertexesData();

        headVertexBuffer.position(0);
        textureCoordBuffer.position(0);

        //设置条目头部的纹理
        if (this.headTextureIds == null) {
            this.headTextureIds = new int[this.origamiView.getOrigamiItems().size()];
            GLES20.glGenTextures(this.headTextureIds.length, this.headTextureIds, 0);
        }
    }

    private void draw() {
        /**
         * 绘制头部
         */
        this.headShader.useProgram();

        int aPosition = this.headShader.getHandle("aPosition");
        int aColor = this.headShader.getHandle("aColor");
        int aTextureCoord = this.headShader.getHandle("aTextureCoord");

        //设置标题顶点
        GLES20.glVertexAttribPointer(aPosition, 3, GLES20.GL_FLOAT, false, 0,
                headVertexBuffer);
        GLES20.glEnableVertexAttribArray(aPosition);

        GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        for (int i = 0; i < headTextureIds.length; i++) {
            GLES20.glVertexAttribPointer(aTextureCoord, 2, GLES20.GL_FLOAT, false,
                    0, textureCoordBuffer);
            GLES20.glEnableVertexAttribArray(aTextureCoord);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, headTextureIds[i]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            Bitmap texture = this.origamiView.getOrigamiItems().get(i).getHeadBitmap();
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, texture, 0);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, i * 4, 4);
        }
    }
}
