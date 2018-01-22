/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package faceapp;

import Luxand.FSDK;
import Luxand.FSDK.HImage;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Devil's Home
 */
public class FaceApp {

    /**
     * @param args the command line arguments
     */
    static double features[];
    static FSDK.HImage imageHandle;
    static FSDK.FSDK_Features.ByReference facialFeatures;
    static double input[][],hiddenWts[][],outputWts[][];
    static int iSize,hSize,oSize;
    static double oi[],oh[],oo[];
    public static double distance(int x1,int y1,int x2,int y2){
        return Math.sqrt((y2-y1)*(y2-y1)+(x2-x1)*(x2-x1));
    }
    static double sigmoid(double x){
        double ans=1.0/(1+Math.pow(Math.E, -x));
        return ans;
    }
    static void feedForward(int row){
       for(int i=0;i<iSize-1;i++)
           oi[i]=input[row][i];
       for(int j=0;j<hSize-1;j++){
           oh[j]=0;
           for(int i=0;i<iSize;i++)
               oh[j]+=hiddenWts[i][j]*oi[i];
           oh[j]=sigmoid(oh[j]);
       }
       for(int k=0;k<oSize;k++){
           oo[k]=0;
           for(int j=0;j<hSize;j++)
               oo[k]+=oh[j]*outputWts[j][k];
           oo[k]=sigmoid(oo[k]);
       }
    }
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        //Neural network init code
        iSize=8;
        oSize=1;
        hSize=3;
        iSize++;
        hSize++;
        hiddenWts=new double[iSize][hSize];
        outputWts=new double[hSize][oSize];
        input=new double[1][8];
        oi=new double[iSize];
        oh=new double[hSize];
        oo=new double[oSize];
        oh[hSize-1]=1;
        oi[iSize-1]=1;
        Scanner sc=new Scanner(new File("E://ML faces sample//weights.txt"));
        for(int i=0;i<iSize;i++)
            for(int j=0;j<hSize;j++)
                hiddenWts[i][j]=sc.nextDouble();
        for(int i=0;i<hSize;i++)
            for(int j=0;j<oSize;j++)
                outputWts[i][j]=sc.nextDouble();
        //init over
        //FaceSdk init code
        int r = FSDK.ActivateLibrary("I08rUOyGBkKK5kwNFsL5dW7OfsA3ay4qag0cVExHXwvyL/T+4jL3P5pvbgdgOLFqxpf04CQqi6UzK8ArlW0rjVvQEHfRV9RrzFgbiTh+B2CzbOcpXmbO+Zmh0LBX+7L4igdDshKeF35dFX6vmupzloaK5ziAFlfwa8HrxkK5+jg=");
        FSDK.Initialize();
        FSDK.SetFaceDetectionParameters(true, true, 384); 
        //FaceSDK code over
        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        WebcamPanel panel = new WebcamPanel(webcam);
	panel.setFPSDisplayed(true);
	panel.setDisplayDebugInfo(true);
	panel.setImageSizeDisplayed(true);
	panel.setMirrored(true);
        JPanel jp1=new JPanel();
        JPanel jp2=new JPanel();
        JButton startButton = new JButton("Capture");
        JButton testButton = new JButton("Test   ");
        JFrame window = new JFrame("Test webcam panel"); 
        window.add(jp1,BorderLayout.PAGE_START);
        window.add(jp2,BorderLayout.PAGE_END);
        jp1.add(panel);
        jp2.add(testButton,BorderLayout.PAGE_START);
        jp2.add(startButton,BorderLayout.PAGE_END);
        JLabel jlab=new JLabel();jlab.setText("Result Here");
        jp2.add(jlab);
                //window.add(startButton,BorderLayout.PAGE_END);
                //window.add(testButton,BorderLayout.PAGE_END);
		//window.add(panel,BorderLayout.PAGE_START);
	window.setResizable(true);
	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	window.pack();
	window.setVisible(true);
        startButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String path="E://ML faces sample//app//1.jpg";
                BufferedImage image = webcam.getImage();
                try {
                    ImageIO.write(image, "JPG", new File(path));
                } catch (IOException ex) {
                    Logger.getLogger(FaceApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        testButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
            imageHandle=new HImage();
            if (FSDK.LoadImageFromFile(imageHandle,"E://ML faces sample//app//1.jpg") == FSDK.FSDKE_OK){
               double eyeBrowEye,eyeBrowNose,noseMouth,eyeMouth,eyetoeye,widthMouth,nosewidth,smlwdth;
               double normalizex,normalizey;
               double arr[]=new double[8];
               facialFeatures = new FSDK.FSDK_Features.ByReference();
               FSDK.DetectFacialFeatures(imageHandle, facialFeatures);                                                                                                                                                                  
               normalizex=distance(facialFeatures.features[5].x,facialFeatures.features[5].y,facialFeatures.features[6].x,facialFeatures.features[6].y);
               normalizey=distance(facialFeatures.features[11].x,facialFeatures.features[11].y,facialFeatures.features[22].x,facialFeatures.features[22].y);
               input[0][0]=eyeBrowEye=(distance(facialFeatures.features[0].x,facialFeatures.features[0].y,facialFeatures.features[16].x,facialFeatures.features[16].y)/normalizey+distance(facialFeatures.features[1].x,facialFeatures.features[1].y,facialFeatures.features[17].x,facialFeatures.features[17].y)/normalizey)/2.0;
               input[0][1]=eyeBrowNose=(distance(facialFeatures.features[2].x,facialFeatures.features[2].y,facialFeatures.features[16].x,facialFeatures.features[16].y)+distance(facialFeatures.features[2].x,facialFeatures.features[2].y,facialFeatures.features[17].x,facialFeatures.features[17].y))/(2.0*normalizey);
               input[0][2]=noseMouth=distance(facialFeatures.features[2].x,facialFeatures.features[2].y,facialFeatures.features[64].x,facialFeatures.features[64].y)/normalizey;
               input[0][3]=eyeMouth=(distance(facialFeatures.features[0].x,facialFeatures.features[0].y,facialFeatures.features[64].x,facialFeatures.features[64].y)+distance(facialFeatures.features[1].x,facialFeatures.features[1].y,facialFeatures.features[64].x,facialFeatures.features[64].y))/(2.0*normalizey);
               input[0][4]=eyetoeye=distance(facialFeatures.features[0].x,facialFeatures.features[0].y,facialFeatures.features[1].x,facialFeatures.features[1].y)/normalizex;
               input[0][5]=widthMouth=distance(facialFeatures.features[3].x,facialFeatures.features[3].y,facialFeatures.features[4].x,facialFeatures.features[4].y)/normalizex;
               input[0][6]=nosewidth=distance(facialFeatures.features[45].x,facialFeatures.features[45].y,facialFeatures.features[46].x,facialFeatures.features[46].y)/normalizex;
               input[0][7]=smlwdth=distance(facialFeatures.features[3].x,facialFeatures.features[3].y,facialFeatures.features[4].x,facialFeatures.features[4].y)/normalizex;
               for(int i=0;i<8;i++){
                   System.out.print(input[0][i]+" ");
               }
               System.out.println("");
               feedForward(0);                                                                                                                                                                                                                                                                                                                                                                                    
               if(Math.abs(1-oo[0])<Math.abs(0-oo[0])){
                jlab.setText("Subject is Female");
                }
            else
                jlab.setText("Subject is Male");
           }
           else{
            System.out.println("Error");
           }
                System.out.println("Test Complete");
            }
        });
    }                                                               
}
