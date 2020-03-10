package uit.vinh.kk;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.SystemClock;
import android.os.Trace;
import android.util.Log;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;


import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeOp.ResizeMethod;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.image.ops.Rot90Op;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.nio.channels.FileChannel;

public class Classifier {
    private static final int BATCH_SIZE = 1;
    private static final int PIXEL_SIZE = 3;

    private static final Logger LOGGER = new Logger();
    private boolean quant = false;

    private ByteBuffer imgData = null;
    private int DIM_IMG_SIZE_X = 224;
    private int DIM_IMG_SIZE_Y = 224;
    private int[] intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];
    /** Number of results to show in the UI. */
    private static final int MAX_RESULTS = 5;
    /** The loaded TensorFlow Lite model. */
    private MappedByteBuffer tfliteModel;
    /** Image size along the x axis. */
    private final int imageSizeX;


    private float[][] labelProbArray = null;
    private static final int RESULTS_TO_SHOW = 5;
    // priority queue that will hold the top results from the CNN
    private PriorityQueue<Map.Entry<String, Float>> sortedLabels =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });


    private List<String> labelList;




    /** Image size along the y axis. */
    private final int imageSizeY;

    /** An instance of the driver class to run model inference with Tensorflow Lite. */
    protected Interpreter tflite;

    /** Options for configuring the Interpreter. */
    private final Interpreter.Options tfliteOptions = new Interpreter.Options();

    /** Labels corresponding to the output of the vision model. */
    private List<String> labels;

    /** Input image TensorBuffer. */
    private TensorImage inputImageBuffer;

    /** Output probability TensorBuffer. */
    private final TensorBuffer outputProbabilityBuffer;

    /** Processer to apply post processing of the output probability. */
    private final TensorProcessor probabilityProcessor;

    private int numThreads = 1;


    /** Float MobileNet requires additional normalization of the used input. */
    private static final float IMAGE_MEAN = 0.0f;

    private static final float IMAGE_STD = 255.0f;

    /**
     * Float model does not need dequantization in the post-processing. Setting mean and std as 0.0f
     * and 1.0f, repectively, to bypass the normalization.
     */
    private static final float PROBABILITY_MEAN = 0.0f;

    private static final float PROBABILITY_STD = 1.0f;

    /**
     * Creates a classifier with the provided configuration.
     *
     * @param activity The current Activity.
     * @return A classifier with the desired configuration.
     */
    public static Classifier create(Activity activity,AssetManager assetManager)
            throws IOException {

        return new Classifier(activity);
    }

    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(getModelPath());
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


    public Classifier(Activity activity) throws IOException {
//        tfliteModel = FileUtil.loadMappedFile(activity, getModelPath());
        String modelFilename = "best_modelHieu.tflite";
        //tfliteOptions.setNumThreads(numThreads);

        tflite = new Interpreter(loadModelFile(activity), tfliteOptions);
        imgData =
                ByteBuffer.allocateDirect(
                        4 * BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * PIXEL_SIZE);
        imgData.order(ByteOrder.nativeOrder());
        // Loads labels out from the label file.
        labels = FileUtil.loadLabels(activity, getLabelPath());

        // Reads type and shape of input and output tensors, respectively.
        int imageTensorIndex = 0;
        int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}

        imageSizeY = imageShape[1];
        imageSizeX = imageShape[2];
        Log.d("debug", "Classifier: imageSizeX == " + imageSizeX);
        Log.d("debug", "Classifier: imageSizeY == " + imageSizeY);
        DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();
        Log.d("debug", "Classifier: Datatype " + imageDataType.toString());
        int probabilityTensorIndex = 0;
        int[] probabilityShape =
                tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
        DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

        // Creates the input tensor.
        inputImageBuffer = new TensorImage(imageDataType);

        // Creates the output tensor and its processor.
        outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);

        // Creates the post processor for the output probability.
        probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();

        labelProbArray = new float[1][5];
        labelList = new ArrayList<String>();
        labelList.add("No DR");
        labelList.add("Mild");
        labelList.add("Moderate");
        labelList.add("Severe");
        labelList.add("Proliferative");

    }

    protected TensorOperator getPreprocessNormalizeOp() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }

    private TensorOperator getPostprocessNormalizeOp(){
        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
    }

    private String getModelPath(){
        return "best_modelHieu.tflite";
    }

    private String getLabelPath(){
        return "best_model_labels.txt";
    }

    /** An immutable result returned by a Classifier describing what was recognized. */
    public static class Recognition {
        /**
         * A unique identifier for what has been recognized. Specific to the class, not the instance of
         * the object.
         */
        private final String id;

        /** Display name for the recognition. */
        private final String title;

        /**
         * A sortable score for how good the recognition is relative to others. Higher should be better.
         */
        private final Float confidence;

        /** Optional location within the source image for the location of the recognized object. */
        private RectF location;

        public Recognition(
                final String id, final String title, final Float confidence, final RectF location) {
            this.id = id;
            this.title = title;
            this.confidence = confidence;
            this.location = location;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Float getConfidence() {
            return confidence;
        }

        public RectF getLocation() {
            return new RectF(location);
        }

        public void setLocation(RectF location) {
            this.location = location;
        }

        @Override
        public String toString() {
            String resultString = "";
            if (id != null) {
                resultString += "[" + id + "] ";
            }

            if (title != null) {
                resultString += title + " ";
            }

            if (confidence != null) {
                resultString += String.format("(%.1f%%) ", confidence * 100.0f);
            }

            if (location != null) {
                resultString += location + " ";
            }

            return resultString.trim();
        }
    }

    /** Runs inference and returns the classification results. */
    public List<Recognition> recognizeImage(final Bitmap bitmap) {
        // Logs this method so that it can be analyzed with systrace.
        Trace.beginSection("recognizeImage");

        Trace.beginSection("loadImage");
        long startTimeForLoadImage = SystemClock.uptimeMillis();

        long endTimeForLoadImage = SystemClock.uptimeMillis();
        Trace.endSection();
        LOGGER.v("Timecost to load the image: " + (endTimeForLoadImage - startTimeForLoadImage));

        // Runs the inference call.
        Trace.beginSection("runInference");
        long startTimeForReference = SystemClock.uptimeMillis();
//        Log.d("vinhdeptrai", "recognizeImage: input image type" + convertBitmapToByteBuffer(bitmap));
        //tflite.run(inputImageBuffer.getBuffer(), outputProbabilityBuffer.getBuffer().rewind());

        ByteBuffer byteBuffer = convertBitmapToByteBuffer(bitmap);
        inputImageBuffer.load(bitmap);
        float[][] result = new float[1][5];
        Log.d("debug", "recognizeImage: byteBuffer == "+ byteBuffer);
        //convertBitmapToByteBuffer(bitmap);
        tflite.run(inputImageBuffer.getBuffer(),outputProbabilityBuffer.getBuffer().rewind());
        Log.d("debug", "recognizeImage: outputProbabilityBuffer.getBuffer().rewind() == "+ result);
        Log.d("debug", "recognizeImage: inputImageBuffer.getBuffer == " + outputProbabilityBuffer.getBuffer().rewind());
        long endTimeForReference = SystemClock.uptimeMillis();
        Trace.endSection();
        LOGGER.v("Timecost to run model inference: " + (endTimeForReference - startTimeForReference));

        // Gets the map of label and probability.
        Map<String, Float> labeledProbability =
                new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
                        .getMapWithFloatValue();
        Log.d("debug", "recognizeImage: labels == "+ labels);
        Log.d("", "recognizeImage: probabilityProcessor.process(outputProbabilityBuffer)" + probabilityProcessor.process(outputProbabilityBuffer));
        Trace.endSection();

//        // test another code about classify
//        tflite.run(inputImageBuffer.getBuffer(), labelProbArray);
//        tempPrintTopKLabels();

        // Gets top-k results.
        return getTopKProbability(labeledProbability);
    }


    /** Gets the top-k results. */
    private static List<Recognition> getTopKProbability(Map<String, Float> labelProb) {
        // Find the best classifications.
        PriorityQueue<Recognition> pq =
                new PriorityQueue<>(
                        MAX_RESULTS,
                        new Comparator<Recognition>() {
                            @Override
                            public int compare(Recognition lhs, Recognition rhs) {
                                // Intentionally reversed to put high confidence at the head of the queue.
                                return Float.compare(rhs.getConfidence(), lhs.getConfidence());
                            }
                        });

        for (Map.Entry<String, Float> entry : labelProb.entrySet()) {
            pq.add(new Recognition("" + entry.getKey(), entry.getKey(), entry.getValue(), null));
        }

        final ArrayList<Recognition> recognitions = new ArrayList<>();
        int recognitionsSize = Math.min(pq.size(), MAX_RESULTS);
        for (int i = 0; i < recognitionsSize; ++i) {
            recognitions.add(pq.poll());
        }
        return recognitions;
    }

    /** Loads input image, and applies preprocessing. */
    private TensorImage loadImage(final Bitmap bitmap, int sensorOrientation) {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap);

        // Creates processor for the TensorImage.
        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        int numRoration = sensorOrientation / 90;
        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeMethod.NEAREST_NEIGHBOR))
                        .add(new Rot90Op(numRoration))
                        .add(getPreprocessNormalizeOp())
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }




    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * PIXEL_SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_X; ++j) {
                final int val = intValues[pixel++];
                byteBuffer.putFloat((((val >> 16) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                byteBuffer.putFloat((((val >> 8) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                byteBuffer.putFloat((((val) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
            }
        }
        return byteBuffer;

    }


}
