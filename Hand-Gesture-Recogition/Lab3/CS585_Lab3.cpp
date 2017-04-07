/**
CS440_PA1
@author: Wei Wei, Jason Huang
@version: 1.0 9/17/2014

Lab 3
--------------
This program introduces the following concepts:
- recognizing hand from the background and recognize the Rock, Scissor and Paper gesture.
--------------
*/

#include "stdafx.h"
#include "opencv2/core/core.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <vector>

using namespace cv;
using namespace std;

//function declarations
/**
Function that returns the maximum of 3 integers
*/
int myMax(int a, int b, int c);

/**
Function that returns the minimum of 3 integers
*/
int myMin(int a, int b, int c);

/**
Function that detects whether a pixel belongs to the skin based on RGB values
@param src The source color image
@param dst The destination grayscale image where skin pixels are colored white and the rest are colored black
*/
void mySkinDetect(Mat& src, Mat& dst);

void condefects(vector<Vec4i> convexityDefectsSet, vector<Point> mycontour, Mat &frame);

//Global variables
int thresh = 128;
int max_thresh = 255;

// main function
int main()
{
	VideoCapture cap(0);

	// if not successful, exit program
	if (!cap.isOpened())
	{
		cout << "Cannot open the video cam" << endl;
		return -1;
	}

	// Mat object to read first frame
	Mat frame0;

	// read a new frame from video
	bool bSuccess0 = cap.read(frame0);

	//if not successful, break loop
	if (!bSuccess0)
	{
		cout << "Cannot read a frame from video stream" << endl;
	}

	//create a window called "RockScissorPaper"
	namedWindow("source", WINDOW_AUTOSIZE);
	namedWindow("RockScissorPaper", WINDOW_AUTOSIZE);

	while (1)
	{
		// read a new frame from video
		Mat frame;
		bool bSuccess = cap.read(frame);

		//if not successful, break loop
		if (!bSuccess)
		{
			cout << "Cannot read a frame from video stream" << endl;
			break;
		}

		// destination frame
		Mat frameDest;
		frameDest = Mat::zeros(frame.rows, frame.cols, CV_8UC1); //Returns a zero array of same size as src mat, and of type CV_8UC1

		// Skin color detection
		mySkinDetect(frame, frameDest);

		// Convert into binary image using thresholding
		// Documentation for threshold: http://docs.opencv.org/modules/imgproc/doc/miscellaneous_transformations.html?highlight=threshold#threshold
		// Example of thresholding: http://docs.opencv.org/doc/tutorials/imgproc/threshold/threshold.html
		Mat thres_output;
		threshold(frameDest, thres_output, thresh, max_thresh, 0);

		vector<vector<Point>> contours;
		vector<Vec4i> hierarchy;
		// Find contours
		// Documentation for finding contours: http://docs.opencv.org/modules/imgproc/doc/structural_analysis_and_shape_descriptors.html?highlight=findcontours#findcontours
		findContours(thres_output, contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE, Point(0, 0));
		cout << "The number of contours detected is: " << contours.size() << endl;

		//Mat frameDest = Mat::zeros(thres_output.size(), CV_8UC3);
		// Find largest contour
		int maxsize = 0;
		int maxind = 0;
		Rect boundrec;
		for (int i = 0; i < contours.size(); i++)
		{
			// Documentation on contourArea: http://docs.opencv.org/modules/imgproc/doc/structural_analysis_and_shape_descriptors.html#
			double area = contourArea(contours[i]);
			if (area > maxsize) {
				maxsize = area;
				maxind = i;
				boundrec = boundingRect(contours[i]);
			}
		}

		/// Find the convex hull and defect object for each contour
		vector<vector<Point> >hull(contours.size());
		vector<vector<Vec4i>>defects(contours.size());
		vector<vector<int> >inthull(contours.size());
		for (int i = 0; i < contours.size(); i++)
		{
			convexHull(Mat(contours[i]), hull[i], false);
			convexHull(Mat(contours[i]), inthull[i], false);
			if (inthull[i].size() > 3) {
				convexityDefects(contours[i], inthull[i], defects[i]);
			}
		}

		// Draw contours
		// Documentation for drawing contours: http://docs.opencv.org/modules/imgproc/doc/structural_analysis_and_shape_descriptors.html?highlight=drawcontours#drawcontours
		// Documentation for drawing rectangle: http://docs.opencv.org/modules/core/doc/drawing_functions.html
		rectangle(frame, boundrec, Scalar(0, 255, 0), 1, 8, 0);
		drawContours(frame, hull, maxind, Scalar(0, 0, 255), 2, 8, hierarchy);
		rectangle(frameDest, boundrec, Scalar(255, 255, 255), 1, 8, 0);
		drawContours(frameDest, hull, maxind, Scalar(255, 0, 255), 2, 8, hierarchy);
		//call the condefect function which plot the 
		condefects(defects[maxind], contours[maxind], frame);

		cout << "The area of the largest contour detected is: " << contourArea(contours[maxind]) << endl;
		cout << "-----------------------------" << endl << endl;

		/// Show in a window
		imshow("RockScissorPaper", frame);
		imshow("source", frameDest);
		if (waitKey(30) == 27)
		{
			cout << "esc key is pressed by user" << endl;
			break;
		}
	}

	// Wait until keypress
	waitKey(0);
	cap.release();
	return 0;
}

//Function that returns the maximum of 3 integers
int myMax(int a, int b, int c) {
	return max(max(a, b), c);
}

//Function that returns the minimum of 3 integers
int myMin(int a, int b, int c) {
	return min(min(a, b), c);
}

//Function that detects whether a pixel belongs to the skin based on RGB values
void mySkinDetect(Mat& src, Mat& dst) {
	//Surveys of skin color modeling and detection techniques:
	//Vezhnevets, Vladimir, Vassili Sazonov, and Alla Andreeva. "A survey on pixel-based skin color detection techniques." Proc. Graphicon. Vol. 3. 2003.
	//Kakumanu, Praveen, Sokratis Makrogiannis, and Nikolaos Bourbakis. "A survey of skin-color modeling and detection methods." Pattern recognition 40.3 (2007): 1106-1122.
	for (int i = 0; i < src.rows; i++){
		for (int j = 0; j < src.cols; j++){
			//For each pixel, compute the average intensity of the 3 color channels
			Vec3b intensity = src.at<Vec3b>(i, j); //Vec3b is a vector of 3 uchar (unsigned character)
			int B = intensity[0]; int G = intensity[1]; int R = intensity[2];
			if ((R > 95 && G > 40 && B > 20) && (myMax(R, G, B) - myMin(R, G, B) > 15) && (abs(R - G) > 15) && (R > G) && (R > B)){
				dst.at<uchar>(i, j) = 255;
			}
		}
	}
}

void condefects(vector<Vec4i> Defects, vector<Point> contour, Mat &frame)
{
	Point2f center;
	float x;
	int fingers = 0;

	// find the centorid of the hand 
	minEnclosingCircle(contour, center, x);
	circle(frame, center, 10, CV_RGB(0, 0, 255), 2, 8);

	for (int i = 0; i < Defects.size(); i++) {

		//extracting the start point and the depth from the Defects
		Point ptStart(contour[Defects[i].val[0]]);
		double depth = static_cast<double>(Defects[i].val[3]) / 256;
		//display start points
		circle(frame, ptStart, 5, CV_RGB(255, 0, 0), 2, 8);

		//if the depth > 11 and the start point is higher than the center, count that as a finger
		if (depth>11 && ptStart.y<center.y) {
			circle(frame, ptStart, 4, CV_RGB(255, 0, 0), 4);
			fingers++;
		}
	}

	//index: if number if fingers detected: (0,1):Rock, (2,3):Scussor, (>3):Paper
	if (fingers >1 && fingers <= 3) {
		putText(frame, "Scissor", Point(50, 50), 2, 2, CV_RGB(0, 255, 0), 4, 8);
	}
	else if (fingers <= 1) {
		putText(frame, "Rock", Point(50, 50), 2, 2, CV_RGB(255, 0, 0), 4, 8);
	}
	else if (fingers > 3) {
		putText(frame, "Paper", Point(50, 50), 2, 2, CV_RGB(0, 0, 255), 4, 8);
	}
}