import numpy as np
import skimage.io as io
from skimage.color import rgb2gray
from skimage.measure import structural_similarity as ssim
from scipy import ndimage
from skimage import filter

import cv2

import sys


# Your Agent for solving Raven's Progressive Matrices. You MUST modify this file.
#
# You may also create and submit new files in addition to modifying this file.
#
# Make sure your file retains methods with the signatures:
# def __init__(self)
# def Solve(self,problem)
#
# These methods will be necessary for the project's main method to run.
class Agent:
    # The default constructor for your Agent. Make sure to execute any
    # processing necessary before your Agent starts solving problems here.
    #
    # Do not add any variables to this signature; they will not be used by
    # main().
    shapeList = ['circle', 'triangle', 'square', 'other']
    def __init__(self):
        
        pass



    def mod(self, num):
        if(num < 0): return -num;
        return num


    def contourInfo(self, image, figLabel, infoTable, infoList):
        # im = cv2.imread('1.png')
        
        imgray = cv2.cvtColor(image,cv2.COLOR_BGR2GRAY)
    
        """
        for i in range(0, imgray.shape[0]):
            for j in range(0, imgray.shape[1]):
                print imgray[i,j],
        print "---------------------------------------------------------------------"
        """

        # print imgray
        # sys.exit()
        # imgray = (255 - imgray)

        ret,thresh = cv2.threshold(imgray,127,255,cv2.THRESH_TOZERO_INV)
        contours, hierarchy = cv2.findContours(thresh,cv2.RETR_TREE,cv2.CHAIN_APPROX_SIMPLE)

        # print figLabel,"->",len(contours)
        # Experiments
        # if (True): return;
        
        """infoList = [shape, numberOfContours(approx. for shape), value-of-contour]"""

        """--------shape------------"""                
        if (len(contours) > 20): # is a circle/complex object
            infoList.append(self.shapeList.index('circle'));
        
        elif (len(contours) == 3): # is a circle/complex object
            infoList.append(self.shapeList.index('triangle'));
        
        # elif (len(contours) == 0): # is a circle/complex object
            # infoList.append(self.shapeList.index('triangle'));
        
        else:
            infoList.append(self.shapeList.index('other'));


        """--------number of contours------------"""
        infoList.append(len(contours))

        
        """---------value-of-contour-------------"""
        if (len(contours) > 0):
            infoList.append(contours[0])

        else:
            infoList.append(-1)

        infoTable[figLabel] = infoList
        #for i in range(0, len(contours)):
           # print 'shape->',contours[i].shape
           #print contours[i] 

        # print '----------------------------------'

    
    ##--------------------------------------------------------------------------------------------####

    def analyzeContourChanges(self, contourInfoTable, figLabel1, figLabel2):
        changeList = []


        fig1Shape = contourInfoTable[figLabel1][0]
        fig2Shape = contourInfoTable[figLabel2][0]

        fig1NumberOfContours = contourInfoTable[figLabel1][1]
        fig2NumberOfContours = contourInfoTable[figLabel2][1]
        # sys.exit()


        ## default values
        fig1ContourLocation_x0 = 0
        fig1ContourLocation_y0 = 0
        fig2ContourLocation_x0 = 0
        fig2ContourLocation_y0 = 0

        fig1ContourLocation_x1 = 0
        fig1ContourLocation_y1 = 0
        fig2ContourLocation_x1 = 0
        fig2ContourLocation_y1 = 0


        if (fig1NumberOfContours != 0):
            fig1ContourLocation_x0 = contourInfoTable[figLabel1][2][0][0][0]
            fig1ContourLocation_y0 = contourInfoTable[figLabel1][2][0][0][1]

        if (fig2NumberOfContours != 0):
            fig2ContourLocation_x0 = contourInfoTable[figLabel2][2][0][0][0]
            fig2ContourLocation_y0 = contourInfoTable[figLabel2][2][0][0][1]

        
        if (fig1NumberOfContours != 0):
            fig1ContourLocation_x1 = contourInfoTable[figLabel1][2][0][-1][0]
            fig1ContourLocation_y1 = contourInfoTable[figLabel1][2][0][-1][1]

        if (fig2NumberOfContours != 0):
            fig2ContourLocation_x1 = contourInfoTable[figLabel2][2][0][-1][0]
            fig2ContourLocation_y1 = contourInfoTable[figLabel2][2][0][-1][1]

        
        # print contourInfoTable[figLabel1][2].shape
        # print 

        # sys.exit()

        if (fig1Shape != fig2Shape):
            changeList.append(True)
        else:        
            changeList.append(False)


        if (fig1NumberOfContours != fig2NumberOfContours):
            changeList.append(True)
        else:        
            changeList.append(False)

        if (fig1ContourLocation_x0*fig1ContourLocation_y0*1.0 == fig2ContourLocation_x0*fig2ContourLocation_y0*1.0):
            if(fig1ContourLocation_x1*fig1ContourLocation_y1*1.0 == fig2ContourLocation_x1*fig2ContourLocation_y1*1.0):
                if(changeList[1] == False): # if number of contours changed -> value also changed
                    changeList.append(False)
                else:
                    changeList.append(True)
            else:
                changeList.append(True)        
        else:        
            changeList.append(True)


        return changeList

    ##--------------------------------------------------------------------------------------------###
    """This function is specifically for 3x3"""
    def findPatterns(self, contourInfoTable):

        Pattern_3_3_2 = False
        numberToLookFor = -1 
        Pattern_increasing = False 
        Pattern_decreasing = False

        figures = ['A','B','C','D','E','F','G','H']
        
        numberOfContoursList = []

        
        """ for finding 3-3-2 pattern -> if found return number with '2' bucket"""
        for i in range(0, len(figures)):
            currentNumber = contourInfoTable[figures[i]][1]
            numberOfContoursList.append(currentNumber)
       
        numberOfContoursList.sort()

        # to check if all objects are same, if yes then don't attempt to find the pattern
        if (not((numberOfContoursList[-1] - numberOfContoursList[0]) <= 6)):
            buckets = {}
            buckets[numberOfContoursList[0]]  = 0
            buckets[numberOfContoursList[-1]] = 0
            buckets[numberOfContoursList[3]]  = 0
            for i in range(0, len(numberOfContoursList)):
                rangeList = range(numberOfContoursList[i]-3, numberOfContoursList[i]+3)
                found = False
                for j in range(0, len(rangeList)):
                    if(rangeList[j] in buckets): 
                        found = True                   
                        key = rangeList[j]
                if (not found):
                    buckets[int(numberOfContoursList[i])] = 1
                elif (found):
                    if (numberOfContoursList[i] in buckets): key = numberOfContoursList[i] 
                    buckets[key] +=1                                    
    
            print buckets
            values = buckets.values()
            print values
            if  (len(values) == 3):
                values.sort()
                if ((values[0] == 2) and (values[1] == 3) and (values[2] == 3)):
                    Pattern_3_3_2 = True
            
            valuedKey = -1
            if (Pattern_3_3_2):
                keys = buckets.keys()
                for i in range(0, len(keys)):
                    if (buckets[keys[i]] == 2):
                        valuedKey = keys[i]

        if (Pattern_3_3_2):
            numberToLookFor = valuedKey


        # --------------------- Pattern Increasing/Decreasing ------------------- #
        number_C = contourInfoTable['C'][1] 
        number_B = contourInfoTable['B'][1]
        number_A = contourInfoTable['A'][1]
        
        number_D = contourInfoTable['D'][1] 
        number_E = contourInfoTable['E'][1]
        number_F = contourInfoTable['F'][1]
        
        number_G = contourInfoTable['G'][1] 
        number_H = contourInfoTable['H'][1]
        

        if (number_C > number_B > number_A):
            if (number_F > number_E > number_D):
                if (number_H > number_G):
                    Pattern_increasing = True

        if (number_C < number_B < number_A):
            if (number_F < number_E < number_D):
                if (number_H < number_G):
                    Pattern_decreasing = True
    

        print "Pattern3-3-2:",Pattern_3_3_2
        print "PatternIncreasing:",Pattern_increasing
        print "PatternDecreasing:",Pattern_decreasing

       
        
        # sys.exit()
        return Pattern_3_3_2,numberToLookFor,Pattern_increasing, Pattern_decreasing
 


    def isFillChanged(self, imageA, imageB):
        
        fillChanged = False    

        template = cv2.imread('fill.png',0)
        template = 255 - template

        imageA = cv2.imread(imageA,0)
        imageA = 255 - imageA
        
        imageB = cv2.imread(imageB,0)
        imageB = 255 - imageB
        

        res = cv2.matchTemplate(imageA,template,3) # 'cv2.TM_SQDIFF' - 4
        min_val, max_val_A, min_loc, max_loc = cv2.minMaxLoc(res)

        res = cv2.matchTemplate(imageB,template,3) # 'cv2.TM_SQDIFF' - 4
        min_val, max_val_B, min_loc, max_loc = cv2.minMaxLoc(res)
        
        if (max_val_A >= 0.7 and max_val_B < 0.7):
            fillChanged = True

        if (max_val_A < 0.7 and max_val_B >= 0.7):
            fillChanged = True

        return fillChanged


    ##--------------------------------------------------------------------------------------------####
    def run_imageProcessing(self, problem):
        
        figures = ['A','B','C','D','E','F','G','H']
        options = ['1','2','3','4','5','6']

        figImages = []
        optionImages = []

        type3x3 = False
    
        # print problem.getFigures()["A"].getPath()
        if ("3x3" in problem.getFigures()["A"].getPath()):
            type3x3 = True
            # return

        numberOfFigures = len(figures)
        
        if (not type3x3):
            numberOfFigures = 4 - 1
        
        print problem.getFigures()['A'].getPath()

        # sys.exit()
        
        """" I intend to store contour info for all here"""
        contourInfoTable = {}
        
        for i in range(0, numberOfFigures):
            infoList = []
            
            image = io.imread(problem.getFigures()[figures[i]].getPath())
            figImages.append(rgb2gray(image))
            # print figures[i]
            # print "--------------------"
            self.contourInfo(image, figures[i], contourInfoTable, infoList)    
        

        """
        for i in range(0, 184):
            for j in range(0, 184):
                print (figImages[0])[i][j],
        print "------------------------------------------------"

        sys.exit()

        """

        # print "----------- Options ----------"
        for i in range(0, len(options)):
            infoList1 = []
            # print problem.getFigures()[options[i]].getPath()
            image = io.imread(problem.getFigures()[options[i]].getPath())
            optionImages.append(rgb2gray(image))
            # print options[i]
            # print "--------------------"
            self.contourInfo(image, options[i], contourInfoTable, infoList1)    
        
        # print self.contourInfo
        # Experiments
        if (type3x3): # ['A','B','C' | 'D','E','F'| 'G','H']

            #------------------- Filtering schemes------------------- # 
            
            Pattern_3_3_2 = False
            
            Pattern_increasing = False
            Pattern_decreasing = False

            Pattern_3_3_2,numberToLookFor, Pattern_increasing, Pattern_decreasing = self.findPatterns(contourInfoTable)

            toBeFiltered = []        
            
            
            incThreshold = 0 #contourInfoTable['F'][1] - contourInfoTable['E'][1]
            decThreshold = 0 #contourInfoTable['E'][1] - contourInfoTable['F'][1]
            for i in range(0, len(options)):
                if (Pattern_3_3_2 and (contourInfoTable[options[i]][1] not in range(numberToLookFor-3, numberToLookFor+3))):
                    toBeFiltered.append(True)
                elif (Pattern_increasing and (contourInfoTable[options[i]][1] - contourInfoTable['H'][1]) < incThreshold):
                    toBeFiltered.append(True)
                elif (Pattern_decreasing and (contourInfoTable['H'][1] - contourInfoTable[options[i]][1]) > decThreshold):
                    toBeFiltered.append(True)
                else: toBeFiltered.append(False)
            
            print toBeFiltered
            # using the above info we can make a call
            firstRowB = figImages[0] + figImages[2]
            firstRowA = figImages[0] + figImages[1]     
            
            # cv2.imshow("window title", firstRowB)
            # cv2.waitKey()
    
            # print firstRowB.shape
            # print firstRowA.type
            
            # sys.exit()
            
            secondRowB = figImages[3] + figImages[5]
            secondRowA = figImages[3] + figImages[4]
                 
            diffFirstRow  = ssim(firstRowA,firstRowB)
            diffSecondRow = ssim(secondRowA,secondRowB)

            analogyFirstRow  = []
            analogySecondRow = []

            Max = 100   

            for i in range(0, len(optionImages)):
                offset = 0
                if (toBeFiltered[i]):
                    offset = Max      
               
                lastRowB = figImages[6] + optionImages[i]
                lastRowA = figImages[6] + figImages[7]
                    
                lastRowDiff = ssim(lastRowA,lastRowB)
          
                analogyIndexTuple = (offset +self.mod(diffFirstRow - lastRowDiff), i+1)
          
                analogyFirstRow.append(analogyIndexTuple)

                analogyIndexTuple = (offset + self.mod(diffSecondRow - lastRowDiff), i+1)

                analogySecondRow.append(analogyIndexTuple)

            print "1st Row->",analogyFirstRow
            print ""
            print "2nd Row->",analogySecondRow
            print "-----------------------------------"

            analogyFirstRow  = sorted(analogyFirstRow, key = lambda x: x[0])
            analogySecondRow = sorted(analogySecondRow, key = lambda x: x[0])

            # print "1st Row->",analogyFirstRow
            print ""
            # print "2nd Row->",analogySecondRow

            if (analogyFirstRow[0][0] < analogySecondRow[0][0]): return analogyFirstRow[0][1]
            else: return analogySecondRow[0][1]
           
        # if (True): return;
        # print contourInfoTable

        changeListAB = self.analyzeContourChanges(contourInfoTable, 'A', 'B')
        
        imageA = problem.getFigures()[figures[0]].getPath()
        imageB = problem.getFigures()[figures[1]].getPath()

        changeListAB.append(self.isFillChanged(imageA, imageB))

        # problem.getFigures()[options[i]].getPath()
        # problem.getFigures()[figures[i]].getPath()
        # changeListAB.append()
        print changeListAB

        toBeFiltered = []
        for i in range(0, len(optionImages)):

            imageC = problem.getFigures()[figures[2]].getPath()      
            optionImage = problem.getFigures()[options[i]].getPath()

            changeListC_x = self.analyzeContourChanges(contourInfoTable, 'C', options[i])
            changeListC_x.append(self.isFillChanged(imageC, optionImage))

            print changeListC_x
            if (changeListAB[0] != changeListC_x[0]): # shape of contour -> helps to eliminate non-sense shape matches 
                toBeFiltered.append(True);
            elif (changeListAB[1] != changeListC_x[1]): # number of contours
                toBeFiltered.append(True);
            elif (changeListAB[2] != changeListC_x[2]): # value of contours -> helps to eliminate exact figures
                toBeFiltered.append(True);
            elif (changeListAB[3] != changeListC_x[3]): # value of fill change
                toBeFiltered.append(True);
            else:
                toBeFiltered.append(False);

        diffAB = ssim(figImages[0], figImages[1])
        diffAC = ssim(figImages[0], figImages[2])

        analogyH = [] # which is the best option when looking at-> A:B :: C:?
        analogyV = [] # which is the best option when looking at-> A:C :: B:?

        dictAB = {}
        dictAC = {}
        Max = 10
        """lets calculate diffAB - diff(Ci) : i in 1 to 6"""
        for i in range(0, len(optionImages)):
             analogy = self.mod(diffAB - ssim(figImages[2], optionImages[i]))
             if (toBeFiltered[i] == True): analogy += Max
             dictAB[analogy] = i
             analogyH.append(analogy)

        print analogyH

        """lets calculate diffAC - diff(Bi) : i in 1 to 6"""
        for i in range(0, len(optionImages)):
             analogy = self.mod(diffAC - ssim(figImages[1], optionImages[i]))
             if (toBeFiltered[i] == True): analogy += Max
             dictAC[analogy] = i
             analogyV.append(analogy)

        print analogyV

        analogyH = np.sort(analogyH)
        analogyV = np.sort(analogyV)

        alpha = 0.95
        beta  = 0.05

        confidenceH = alpha*(analogyH[1]/(analogyH[0]+.000001))
        for i in range(2, len(analogyH)):
            confidenceH += beta*((float)(analogyH[i])/(float)(analogyH[0]+.000001))

        confidenceV = alpha*(analogyV[1]/(analogyV[0]+.000001))
        for i in range(2, len(analogyV)):
            confidenceV += beta*((float)(analogyV[i])/(float)(analogyV[0]+.000001))

        print confidenceH
        print dictAB[analogyH[0]]


        print confidenceV
        print dictAC[analogyV[0]]

        if (confidenceH > confidenceV): return 1+dictAB[analogyH[0]];
        return 1+dictAC[analogyV[0]];
        # print analogyH
        # print dictAB[dictAB.keys()[0]]
        # sys.exit()
        """       
        imageA = io.imread(problem.getFigures()['A'].getPath())
        imageB = io.imread(problem.getFigures()['B'].getPath())
        imageC = io.imread(problem.getFigures()['C'].getPath())

        imageA = rgb2gray(imageA)
        imageB = rgb2gray(imageB)
        imageC = rgb2gray(imageC)


        for i in range
            imageA = io.imread(problem.getFigures()["A"].getPath())        
        """
        """
        image1 = io.imread(problem.getFigures()["A"].getPath())
        image2 = io.imread(problem.getFigures()["A"].getPath())
        image3 = io.imread(problem.getFigures()["A"].getPath())
        image4 = io.imread(problem.getFigures()["A"].getPath())
        image5 = io.imread(problem.getFigures()["A"].getPath())
        image6 = io.imread(problem.getFigures()["A"].getPath())
        # imageD = io.imread('D.png')
        imageA = rgb2gray(imageA)
        imageB = rgb2gray(imageB)
        imageC = rgb2gray(imageC)
        image1 = rgb2gray(image1)
        image2 = rgb2gray(image2)
        image3 = rgb2gray(image3)
        image4 = rgb2gray(image4)
        image5 = rgb2gray(image5)
        image6 = rgb2gray(image6)

        # mse(imageA, imageB)
        diffAB = ssim(imageA, imageB)
        print ssim(imageA, imageB)# + ssim(imageA, imageC))
        print "--------------------"    
        print self.mod(diffAB - ssim(imageC, image1)) #+ ssim(imageB, image1))
        print self.mod(diffAB - ssim(imageC, image2)) #+ ssim(imageB, image2))
        print self.mod(diffAB - ssim(imageC, image3)) #+ ssim(imageB, image3))
        print self.mod(diffAB - ssim(imageC, image4)) #+ ssim(imageB, image4))
        print self.mod(diffAB - ssim(imageC, image5)) #+ ssim(imageB, image5))
        print self.mod(diffAB - ssim(imageC, image6)) #+ ssim(imageB, image6))

        print "--------------------"
        
        diffAC = ssim(imageA, imageC)
        print ssim(imageA, imageC)
        print "--------------------"
        
        print self.mod(diffAC - ssim(imageB, image1))
        print self.mod(diffAC - ssim(imageB, image2))
        print self.mod(diffAC - ssim(imageB, image3))
        print self.mod(diffAC - ssim(imageB, image4))
        print self.mod(diffAC - ssim(imageB, image5))
        print self.mod(diffAC - ssim(imageB, image6))
        """

    #-------------------------------------------------------------------------------#

    # The primary method for solving incoming Raven's Progressive Matrices.
    # For each problem, your Agent's Solve() method will be called. At the
    # conclusion of Solve(), your Agent should return a String representing its
    # answer to the question: "1", "2", "3", "4", "5", or "6". These Strings
    # are also the Names of the individual RavensFigures, obtained through
    # RavensFigure.getName().
    #
    # In addition to returning your answer at the end of the method, your Agent
    # may also call problem.checkAnswer(String givenAnswer). The parameter
    # passed to checkAnswer should be your Agent's current guess for the
    # problem; checkAnswer will return the correct answer to the problem. This
    # allows your Agent to check its answer. Note, however, that after your
    # agent has called checkAnswer, it will#not* be able to change its answer.
    # checkAnswer is used to allow your Agent to learn from its incorrect
    # answers; however, your Agent cannot change the answer to a question it
    # has already answered.
    #
    # If your Agent calls checkAnswer during execution of Solve, the answer it
    # returns will be ignored; otherwise, the answer returned at the end of
    # Solve will be taken as your Agent's answer to this problem.
    #
    # @param problem the RavensProblem your agent should solve
    # @return your Agent's answer to this problem
    
    def Solve(self,problem):
        answer = self.run_imageProcessing(problem)
        print answer
        # sys.exit()
        return str(answer)
