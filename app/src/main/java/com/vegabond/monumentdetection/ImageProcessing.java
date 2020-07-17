package com.vegabond.monumentdetection;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.vegabond.monumentdetection.Camera2BasicFragment.count;
import static com.vegabond.monumentdetection.Camera2BasicFragment.setting;
import static com.vegabond.monumentdetection.Camera2BasicFragment.storageDir;
import static com.vegabond.monumentdetection.Camera2BasicFragment.storageDirMain;
import static org.opencv.imgproc.Imgproc.cvtColor;

public class ImageProcessing {


    static String imageProcess(Context mContext){
        String mode = SettingUtility.getControlSettings(mContext).getMode();
        Log.d("ModeCheck","Mode :"+mode);
        switch (mode){
            case "1":
//                Toast.makeText(mContext,"Basic Mode 1 Selected",Toast.LENGTH_SHORT).show();
                return basicMode1();
            case "2":
//                Toast.makeText(mContext,"Basic Mode 2 Selected",Toast.LENGTH_SHORT).show();
                return basicMode2();
            case "3":
//                Toast.makeText(mContext,"Basic Mode 3 Selected",Toast.LENGTH_SHORT).show();
                return basicMode3();
            case "4":
//                Toast.makeText(mContext,"Intermediate Mode 1 Selected",Toast.LENGTH_SHORT).show();
                return intermediateMode1();
            case "5":
//                Toast.makeText(mContext,"Advanced Mode 1 Selected",Toast.LENGTH_SHORT).show();
                return advancedMode1();
            case "6":
//                Toast.makeText(mContext,"Advanced Mode 2 Selected",Toast.LENGTH_SHORT).show();
                return advancedMode2();
            case "7":
//                Toast.makeText(mContext,"Advanced Mode 3 Selected",Toast.LENGTH_SHORT).show();
                return advancedMode3();
            default:
                Toast.makeText(mContext,"Basic Mode 1 Selected",Toast.LENGTH_SHORT).show();
                break;
        }
        return "";

    }

    public static String intermediateMode1(){
        Log.d("ImageProcessing","In After Allignment");
        List<Mat> listImages = new ArrayList<>();
        int startPic = 0;
        int endPic = count-1;
        Log.d("ImageProcessing","Start :"+startPic+" "+"End :"+endPic);
        for (int i=startPic;i<=endPic;i++){
            String file_name = storageDir+"/processed"+i+".jpg";
            Log.d("ImageProcessing","FilePath :"+file_name);
            Mat img = Imgcodecs.imread(file_name);

            listImages.add(img);

        }
        Log.d("ImageProcessing","Images List Size :"+listImages.size());

        double[] rgb_prev = new double[] { 0.0, 0.0, 0.0 };
        double red = 0.0, green = 0.0, blue = 0.0;
        boolean doubelcheck = false;

        for (int i = 0; i < listImages.get(0).rows(); i++) {
            for (int j = 0; j < listImages.get(0).cols(); j++) {

                /*
                 * For Lower Part Of image We use median We use
                 * larger window for comparing as compare to lower part of image
                 */
                if (i > listImages.get(0).rows() / 2) {

                    List<Double> r = new ArrayList<>();
                    List<Double> g = new ArrayList<>();
                    List<Double> b = new ArrayList<>();
                    for (int k = 0; k < listImages.size(); k++) {
                        double[] rgb = listImages.get(k).get(i, j);
                        if (rgb[0] != 0 && rgb[1] != 0 && rgb[2] != 0) {
                            r.add(rgb[0]);
                            g.add(rgb[1]);
                            b.add(rgb[2]);
                            rgb_prev = rgb;
                        }
                    }
                    red = r.size() < 1 ? rgb_prev[0] : medianDouble(r);
                    green = g.size() < 1 ? rgb_prev[1] : medianDouble(g);
                    blue = b.size() < 1 ? rgb_prev[2] : medianDouble(b);

                    // ========New Code==============================
                    double[] rgb_oth = listImages.get(0).get(i, j);
                    double range = 0;
                    if (red >= (rgb_oth[0] - range) && red <= rgb_oth[0] + range && green >= rgb_oth[1] - range
                            && green <= rgb_oth[1] + range && blue >= rgb_oth[2] - range
                            && blue <= rgb_oth[2] + range) {
                        j += 200;
                        doubelcheck = false;
                    } else {
                        listImages.get(0).put(i, j, new double[] { red, green, blue });
                        if (!doubelcheck) {
                            j -= 199;
                            doubelcheck = true;
                        }
                    }
                }
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        final String currentTimeStamp = dateFormat.format(new Date());

        Imgcodecs.imwrite(storageDirMain+"/"+"MONUMENT_"+currentTimeStamp+".jpg", listImages.get(0));
        Log.d("ImageProcessing","Saved :"+storageDirMain+"/"+"MONUMENT_"+currentTimeStamp+".jpg");

        return storageDirMain+"/"+"MONUMENT_"+currentTimeStamp+".jpg";

    }

    public static String basicMode3(){
        Log.d("ImageProcessing","In After Allignment");
        List<Mat> listImages = new ArrayList<>();
        int startPic = 0;
        int endPic = count-1;
        Log.d("ImageProcessing","Start :"+startPic+" "+"End :"+endPic);
        for (int i=startPic;i<=endPic;i++){
            String file_name = storageDir+"/processed"+i+".jpg";
            Log.d("ImageProcessing","FilePath :"+file_name);
            Mat img = Imgcodecs.imread(file_name);

            listImages.add(img);

        }
        Log.d("ImageProcessing","Images List Size :"+listImages.size());

        double[] rgb_prev = new double[] { 0.0, 0.0, 0.0 };
        double red = 0.0, green = 0.0, blue = 0.0;
        boolean doubelcheck = false;

        for (int i = 0; i < listImages.get(0).rows(); i++) {
            for (int j = 0; j < listImages.get(0).cols(); j++) {

                    List<Double> r = new ArrayList<>();
                    List<Double> g = new ArrayList<>();
                    List<Double> b = new ArrayList<>();
                    for (int k = 0; k < listImages.size(); k++) {
                        double[] rgb = listImages.get(k).get(i, j);
                        if (rgb[0] != 0 && rgb[1] != 0 && rgb[2] != 0) {
                            r.add(rgb[0]);
                            g.add(rgb[1]);
                            b.add(rgb[2]);
                            rgb_prev = rgb;
                        }
                    }
                    red = r.size() < 1 ? rgb_prev[0] : medianDouble(r);
                    green = g.size() < 1 ? rgb_prev[1] : medianDouble(g);
                    blue = b.size() < 1 ? rgb_prev[2] : medianDouble(b);

                    // ========New Code==============================
                    double[] rgb_oth = listImages.get(0).get(i, j);
                    double range = 0;
                    if (red >= (rgb_oth[0] - range) && red <= rgb_oth[0] + range && green >= rgb_oth[1] - range
                            && green <= rgb_oth[1] + range && blue >= rgb_oth[2] - range
                            && blue <= rgb_oth[2] + range) {
                        j += 200;
                        doubelcheck = false;
                    } else {
                        listImages.get(0).put(i, j, new double[] { red, green, blue });
                        if (!doubelcheck) {
                            j -= 199;
                            doubelcheck = true;
                        }
                    }

            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        final String currentTimeStamp = dateFormat.format(new Date());

        Imgcodecs.imwrite(storageDirMain+"/"+"MONUMENT_"+currentTimeStamp+".jpg", listImages.get(0));
        Log.d("ImageProcessing","Saved :"+storageDirMain+"/"+"MONUMENT_"+currentTimeStamp+".jpg");

        return storageDirMain+"/"+"MONUMENT_"+currentTimeStamp+".jpg";

    }

    public static String basicMode2(){
        Log.d("ImageProcessing","In After Allignment");
        List<Mat> listImages = new ArrayList<>();
        int startPic = 0;
        int endPic = count-1;
        Log.d("ImageProcessing","Start :"+startPic+" "+"End :"+endPic);
        for (int i=startPic;i<=endPic;i++){
            String file_name = storageDir+"/processed"+i+".jpg";
            Log.d("ImageProcessing","FilePath :"+file_name);
            Mat img = Imgcodecs.imread(file_name);

            listImages.add(img);

        }
        Log.d("ImageProcessing","Images List Size :"+listImages.size());


        for (int i = 0; i < listImages.get(0).rows(); i++) {
            for (int j = 0; j < listImages.get(0).cols(); j++) {

                if (i > listImages.get(0).rows() / 2) {

                    List<Double> r = new ArrayList<>();
                    List<Double> g = new ArrayList<>();
                    List<Double> b = new ArrayList<>();
                    for (int k = 0; k < listImages.size(); k++) {
                        double[] rgb = listImages.get(k).get(i, j);
                        if (rgb[0] != 0 && rgb[1] != 0 && rgb[2] != 0) {
                            r.add(rgb[0]);
                            g.add(rgb[1]);
                            b.add(rgb[2]);
                        }
                    }
                    double red = medianDouble(r);
                    double green = medianDouble(g);
                    double blue = medianDouble(b);
                    listImages.get(0).put(i, j, new double[]{red, green, blue});
                }
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        final String currentTimeStamp = dateFormat.format(new Date());

        Imgcodecs.imwrite(storageDirMain+"/"+"MONUMENT_"+currentTimeStamp+".jpg", listImages.get(0));
        Log.d("ImageProcessing","Saved :"+storageDirMain+"/"+"MONUMENT_"+currentTimeStamp+".jpg");

        return storageDirMain+"/"+"MONUMENT_"+currentTimeStamp+".jpg";
    }

    public static String basicMode1(){
        Log.d("ImageProcessing","In After Allignment");
        List<Mat> listImages = new ArrayList<>();
        int startPic = 0;
        int endPic = count-1;
        Log.d("ImageProcessing","Start :"+startPic+" "+"End :"+endPic);
        for (int i=startPic;i<=endPic;i++){
            String file_name = storageDir+"/processed"+i+".jpg";
            Log.d("ImageProcessing","FilePath :"+file_name);
            Mat img = Imgcodecs.imread(file_name);

            listImages.add(img);

        }
        Log.d("ImageProcessing","Images List Size :"+listImages.size());


        for (int i = 0; i < listImages.get(0).rows(); i++) {
            for (int j = 0; j < listImages.get(0).cols(); j++) {
                List<Double> r = new ArrayList<>();
                List<Double> g = new ArrayList<>();
                List<Double> b = new ArrayList<>();
                for (int k = 0; k < listImages.size(); k++) {
                    double[] rgb = listImages.get(k).get(i, j);
                    if (rgb[0] != 0 && rgb[1] != 0 && rgb[2] != 0) {
                        r.add(rgb[0]);
                        g.add(rgb[1]);
                        b.add(rgb[2]);
                    }
                }
                double red = medianDouble(r);
                double green = medianDouble(g);
                double blue = medianDouble(b);
                listImages.get(0).put(i, j, new double[] { red, green, blue });
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        final String currentTimeStamp = dateFormat.format(new Date());

        Imgcodecs.imwrite(storageDirMain+"/"+"MONUMENT_"+currentTimeStamp+".jpg", listImages.get(0));
        Log.d("ImageProcessing","Saved :"+storageDirMain+"/"+"MONUMENT_"+currentTimeStamp+".jpg");

        return storageDirMain+"/"+"MONUMENT_"+currentTimeStamp+".jpg";
    }

    public static String advancedMode3(){
        Log.d("ImageProcessing","In After Allignment");
        List<short[]> listImages = new ArrayList<>();
        int startPic = 0;
        int endPic = count-1;
        Log.d("ImageProcessing","Start :"+startPic+" "+"End :"+endPic);
        for (int i=startPic;i<=endPic;i++){
            String file_name = storageDir+"/processed"+i+".jpg";
            Log.d("ImageProcessing","FilePath :"+file_name);
            Mat img = Imgcodecs.imread(file_name);

            img.convertTo(img, CvType.CV_16SC3);

            int size = (int) (img.total() * img.channels());
            short[] temp = new short[size];
            img.get(0, 0, temp);

            listImages.add(temp);

        }
        Log.d("ImageProcessing","Images List Size :"+listImages.size());

        boolean double_check = false;

        for (int i = listImages.get(0).length / 2; i < listImages.get(0).length; i++) {
            List<Short> pi = new ArrayList<>();
            for (int j = 0; j < listImages.size(); j++) {
                if (listImages.get(j)[i] != 0) {
                    pi.add(listImages.get(j)[i]);
                }
            }
            Short currentPixel = pi.size() < 1 ? listImages.get(0)[i] : median(pi);
            Short range = 0;
            if (currentPixel >= listImages.get(0)[i] - range && currentPixel <= listImages.get(0)[i] + range) {
                i += 10;
                double_check = false;
            } else {
                listImages.get(0)[i] = currentPixel;
                if (!double_check) {
                    i -= 9;
                    double_check = true;
                }
            }

        }
        String file_name = storageDir+"/processed"+"0"+".jpg";
        System.out.println(file_name);

        Mat finalimg = Imgcodecs.imread(file_name);

        finalimg.convertTo(finalimg, CvType.CV_16SC3);
        finalimg.put(0, 0, listImages.get(0));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        final String currentTimeStamp = dateFormat.format(new Date());

        Imgcodecs.imwrite(storageDirMain+"/"+"MONUMENT_"+currentTimeStamp+".jpg", finalimg);
        Log.d("ImageProcessing","Saved :"+storageDirMain+"/"+"MONUMENT_"+currentTimeStamp+".jpg");

        return storageDirMain+"/"+"MONUMENT_"+currentTimeStamp+".jpg";
    }

    public static String advancedMode2(){
        Log.d("ImageProcessing","In After Allignment");
        List<short[]> listImages = new ArrayList<>();
        int startPic = 0;
        int endPic = count-1;
        Log.d("ImageProcessing","Start :"+startPic+" "+"End :"+endPic);
        for (int i=startPic;i<=endPic;i++){
            String file_name = storageDir+"/processed"+i+".jpg";
            Log.d("ImageProcessing","FilePath :"+file_name);
            Mat img = Imgcodecs.imread(file_name);

            img.convertTo(img, CvType.CV_16SC3);

            int size = (int) (img.total() * img.channels());
            short[] temp = new short[size];
            img.get(0, 0, temp);

            listImages.add(temp);

        }
        Log.d("ImageProcessing","Images List Size :"+listImages.size());

        for (int i = listImages.get(0).length/2; i < listImages.get(0).length; i++) {
            List<Short> pi = new ArrayList<>();
            for (int j = 0; j < listImages.size(); j++) {
                if (listImages.get(j)[i]!=0) {
                    pi.add(listImages.get(j)[i]);
                }
            }
            listImages.get(0)[i] = pi.size()<1?listImages.get(0)[i]:median(pi);

        }
        String file_name = storageDir+"/processed"+"0"+".jpg";
        System.out.println(file_name);

        Mat finalimg = Imgcodecs.imread(file_name);

        finalimg.convertTo(finalimg, CvType.CV_16SC3);
        finalimg.put(0, 0, listImages.get(0));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        final String currentTimeStamp = dateFormat.format(new Date());

        Imgcodecs.imwrite(storageDirMain+"/"+"MONUMENT_"+currentTimeStamp+".jpg", finalimg);
        Log.d("ImageProcessing","Saved :"+storageDirMain+"/"+"MONUMENT_"+currentTimeStamp+".jpg");

        return storageDirMain+"/"+"MONUMENT_"+currentTimeStamp+".jpg";
    }

    public static String advancedMode1(){
        Log.d("ImageProcessing","In After Allignment");
        List<short[]> listImages = new ArrayList<>();
        int startPic = 0;
        int endPic = count-1;
        Log.d("ImageProcessing","Start :"+startPic+" "+"End :"+endPic);
        for (int i=startPic;i<=endPic;i++){
            String file_name = storageDir+"/processed"+i+".jpg";
            Log.d("ImageProcessing","FilePath :"+file_name);
            Mat img = Imgcodecs.imread(file_name);

            img.convertTo(img, CvType.CV_16SC3);

            int size = (int) (img.total() * img.channels());
            short[] temp = new short[size];
            img.get(0, 0, temp);

            listImages.add(temp);

        }
        Log.d("ImageProcessing","Images List Size :"+listImages.size());

        for (int i = 0; i < listImages.get(0).length; i++) {
            List<Short> pi = new ArrayList<>();
            for (int j = 0; j < listImages.size(); j++) {
                if (listImages.get(j)[i]!=0) {
                    pi.add(listImages.get(j)[i]);
                }
            }
            listImages.get(0)[i] = pi.size()<1?listImages.get(0)[i]:median(pi);

        }
        String file_name = storageDir+"/processed"+"0"+".jpg";
        System.out.println(file_name);

        Mat finalimg = Imgcodecs.imread(file_name);

        finalimg.convertTo(finalimg, CvType.CV_16SC3);
        finalimg.put(0, 0, listImages.get(0));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        final String currentTimeStamp = dateFormat.format(new Date());

        Imgcodecs.imwrite(storageDirMain+"/"+"MONUMENT_"+currentTimeStamp+".jpg", finalimg);
        Log.d("ImageProcessing","Saved :"+storageDirMain+"/"+"MONUMENT_"+currentTimeStamp+".jpg");

        return storageDirMain+"/"+"MONUMENT_"+currentTimeStamp+".jpg";
    }


    private static short median(List<Short> values) {
        // TODO Auto-generated method stub
        Collections.sort(values);
        short median = 0;
        int totalElements = values.size();
        if (totalElements % 2 == 0) {
            short sumOfMiddleElements = (short) (values.get(totalElements / 2) + values.get(totalElements / 2 - 1));
            median = (short) (sumOfMiddleElements / 2);
        } else {
            median = values.get(values.size() / 2);
        }
        return median;

    }

    private static double medianDouble(List<Double> values) {
        // TODO Auto-generated method stub
        Collections.sort(values);
        double median = 0;
        int totalElements = values.size();
        if (totalElements % 2 == 0) {
            double sumOfMiddleElements = (double) (values.get(totalElements / 2) + values.get(totalElements / 2 - 1));
            median = (double) (sumOfMiddleElements / 2);
        } else {
            median = values.get(values.size() / 2);
        }
        return median;

    }

    static Mat imageRegistration(Mat img_ref, Mat new_img){

        Mat img_ref_gray=new Mat();
        cvtColor(img_ref,img_ref_gray,Imgproc.COLOR_BGR2GRAY);
//        Imgcodecs.imwrite(storageDir + "/orGrayOrg" + count + ".jpg", img_ref_gray);

        Mat new_img_gray=new Mat();
        cvtColor(new_img,new_img_gray,Imgproc.COLOR_BGR2GRAY);
//        Imgcodecs.imwrite(storageDir + "/nwGrayOrg" + count + ".jpg", new_img_gray);

        ORB orb_detector=ORB.create();

        orb_detector.setMaxFeatures(5000);

        MatOfKeyPoint keypoints_1=new MatOfKeyPoint();
        Mat descriptors_1=new Mat();

        MatOfKeyPoint keypoints_2=new MatOfKeyPoint();
        Mat descriptors_2=new Mat();

        orb_detector.detectAndCompute(img_ref_gray, new Mat(), keypoints_1, descriptors_1);
        orb_detector.detectAndCompute(new_img_gray, new Mat() ,keypoints_2, descriptors_2);
        List<KeyPoint> kp1=keypoints_1.toList();
        List<KeyPoint> kp2=keypoints_2.toList();
        BFMatcher matcher=BFMatcher.create(BFMatcher.BRUTEFORCE_HAMMING, true);
        MatOfDMatch matches=new MatOfDMatch();
        matcher.match(descriptors_1, descriptors_2, matches);

        List<DMatch> match_new=matches.toList();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            match_new.sort(new Compare_Dmatch());
        }
        List<DMatch> final_match=match_new.subList(0, (int)(match_new.size()*0.9));

        List<Point> p1=new ArrayList<Point>(final_match.size());
        List<Point> p2=new ArrayList<Point>(final_match.size());

        for(int j=0;j<final_match.size();j++) {

            int index_1=final_match.get(j).queryIdx;
            p1.add(kp1.get(index_1).pt);

            int index_2=final_match.get(j).trainIdx;
            p2.add(kp2.get(index_2).pt);

        }

        MatOfPoint2f matObject = new MatOfPoint2f();
        matObject.fromList(p1);

        MatOfPoint2f matScene = new MatOfPoint2f();
        matScene.fromList(p2);

        Mat homography = Calib3d.findHomography(matObject, matScene, Calib3d.RANSAC,5.0f);
        if (Core.determinant(homography)!=0) {
            homography = homography.inv();
        }

        Mat transformed_img=new Mat();
        Imgproc.warpPerspective(new_img,transformed_img,homography,new_img.size());

        return transformed_img;
    }
}

class Compare_Dmatch implements Comparator<DMatch> {

    @Override
    public int compare(DMatch o1, DMatch o2) {
        // TODO Auto-generated method stub
        if(o1.distance<o2.distance)
            return -1;
        if(o1.distance>o2.distance)
            return 1;
        return 0;
    }

}
