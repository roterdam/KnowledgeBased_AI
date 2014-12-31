package project3;

import java.util.*;




/**
 * Your Agent for solving Raven's Progressive Matrices. You MUST modify this
 * file.
 * 
 * You may also create and submit new files in addition to modifying this file.
 * 
 * Make sure your file retains methods with the signatures:
 * public Agent()
 * public char Solve(RavensProblem problem)
 * 
 * These methods will be necessary for the project's main method to run.
 * 
 */
public class Agent {
    /**
     * The default constructor for your Agent. Make sure to execute any
     * processing necessary before your Agent starts solving problems here.
     * 
     * Do not add any variables to this signature; they will not be used by
     * main().
     * 
     */
	 // positions = new String[10];
	
	String [] positions = {"inside","above", "overlaps","left-of","leftof","right-of","rightof", 
						   "below", "outside"};
	
	
	String [] figures = {"A","B","C","D","E","F","G","H"};
	String [] choices = {"1","2","3","4","5","6"};
	
	int [] probableChoicesStrategy_1;
	
	HashMap <String, Integer> objectCountMap;
	HashMap <String, Integer> figureCountMap;
	HashMap <String, Integer> attributeCountMap;
	
	int [][]objectAttributeTable;
	int [][]figureAttributeTable;
	
	boolean oneObjectProblem; // to determine if all the figures in a given problem have only one object
	
	boolean figurePatternProblem; // to determine if problem exhibits inter-figure patterns

	
	
	/*------------------------------ 2x2 and 2x1 specific data and functions ------------------------------*/
	
	
    Map<String,RavensFigure> problemMap;
    HashMap<String, String> attributeDiff;
    HashMap<String, String> attributeTable;
    
    HashMap<RavensObject, Integer> diffTableAB;
    HashMap<RavensObject, Integer> diffTableCD;
    
    static Set<String> positionalAttrs = new HashSet<String>();
	
 // declares an array of integers
    int[] choiceMatchArray;
    
    int[] choiceMatchCross;
	
	
    private static void addPosKws()
    {
    	positionalAttrs.add("left-of".toLowerCase());
    	positionalAttrs.add("right-of".toLowerCase());
    	positionalAttrs.add("above".toLowerCase());
    	positionalAttrs.add("right".toLowerCase());
    	positionalAttrs.add("below".toLowerCase());
    	positionalAttrs.add("inside".toLowerCase());
    	// return;
    }
    
    private void addAttributeDiff(String Attribute, String Value)
    {    	
    	
    	attributeDiff.put(Attribute,Value);
    	
    	// System.out.println(Attribute);
    }
    
    
    // the function will fill a HashMap depicting difference between object-1 and object-2 
    private void fillupDiffTable(HashMap<RavensObject, Integer> diffTable, RavensObject obj1, RavensObject obj2, int exchanged)
    {
    	
    	HashMap<String, String> attributeTableObj1 = new HashMap<String,String>();
    	ListIterator<RavensAttribute> attrIter =  obj1.getAttributes().listIterator();
    	RavensAttribute attr;
    	
    	// check if obj2 is null
    	//  if it is then obj1 got deleted
    	// and if exchanged is true then actually the object was added. 
    	if (obj2 == null)
    	{
    		if (exchanged == 1)
    		{
    			// System.out.println("Exchange !");
    			diffTable.put(obj1, new Integer(-1));
    		}
    		else diffTable.put(obj1, new Integer(1));
    		return;
    	}
    	
    	while(attrIter.hasNext())
		{
			  attr = attrIter.next();
			  attributeTableObj1.put(attr.getName().toLowerCase(),attr.getValue()); // attribs : all lower case
			  // numberOfAttributes++;
			  // System.out.println(attr.getName() + "  " + attr.getValue());
		}
    	
    	
    	
    	// Now compare each attribute for obj1 and obj2
    	
    	attrIter =  obj2.getAttributes().listIterator();
    	
    	int diff = 0;
    	int bothCircles = 0;
    	
    	while (attrIter.hasNext()) // iterating over all the attributes of obj2 
    	{
    		attr = attrIter.next();
    		// diff = 0;
    		if (attr.getName().equalsIgnoreCase("shape"))
    		{
    			if (attributeTableObj1.containsKey("shape"))
    			{
    				if (attr.getValue().equalsIgnoreCase(attributeTableObj1.get("shape")))
    				{
    					// No shape difference
    					if (attr.getValue().equalsIgnoreCase("circle"))
    					{
    						bothCircles = 1;
    					}
    					
    					// diff += 9;
    				}
    				else
    				{
    					if ( attr.getValue().equalsIgnoreCase("triangle") && 
       						 attributeTableObj1.get("shape").equalsIgnoreCase("square")
       					   )
    						diff += 10;
       					
    					if ( attr.getValue().equalsIgnoreCase("square") && 
          						 attributeTableObj1.get("shape").equalsIgnoreCase("triangle")
          					   )
       						diff += 20;
    					
    					if ( attr.getValue().equalsIgnoreCase("triangle") && 
          						 attributeTableObj1.get("shape").equalsIgnoreCase("circle")
          					   )
       						diff += 30;
          					
    					if ( attr.getValue().equalsIgnoreCase("circle") && 
         						 attributeTableObj1.get("shape").equalsIgnoreCase("triangle")
         					   )
      						diff += 40;
         				
    					if ( attr.getValue().equalsIgnoreCase("square") && 
         						 attributeTableObj1.get("shape").equalsIgnoreCase("circle")
         					   )
      						diff += 50;
         				
    					if ( attr.getValue().equalsIgnoreCase("circle") && 
        						 attributeTableObj1.get("shape").equalsIgnoreCase("square")
        					   )
     						diff += 60;
    					
    					if ( attr.getValue().equalsIgnoreCase("octagon") && 
       						 attributeTableObj1.get("shape").equalsIgnoreCase("square")
       					   )
    						diff += 70;
    					
    					if ( attr.getValue().equalsIgnoreCase("hexagon") && 
          						 attributeTableObj1.get("shape").equalsIgnoreCase("triangle")
          					   )
       						diff += 70;
          				
    					if ( attr.getValue().equalsIgnoreCase("square") && 
          						 attributeTableObj1.get("shape").equalsIgnoreCase("octagon")
          					   )
       						diff += 75;
    					
    					if ( attr.getValue().equalsIgnoreCase("triangle") && 
          						 attributeTableObj1.get("shape").equalsIgnoreCase("hexagon")
          					   )
       						diff += 75;
       					
       					
    				}
    			}
    			else // unlikely but possible : no shape 
    			{
    				
    			}
    		}
    		
    		if (attr.getName().equalsIgnoreCase("size")) // obj2
    		{
    			if (attributeTableObj1.containsKey("size")) // obj1
    			{
    				
    				if (attr.getValue().equalsIgnoreCase(attributeTableObj1.get("size")))
    				{
    					// No size difference
    					// diff += 9;
    				}
    				else
    				{
    					if ( attr.getValue().equalsIgnoreCase("small") && 
    						 attributeTableObj1.get("size").equalsIgnoreCase("medium")
    					   )
    					diff += 10000;
    					
    					if ( attr.getValue().equalsIgnoreCase("medium") && 
       						 attributeTableObj1.get("size").equalsIgnoreCase("small")
       					   )
       					diff += 20000;
       					
    					
    					else if ( attr.getValue().equalsIgnoreCase("medium") && 
       						 attributeTableObj1.get("size").equalsIgnoreCase("large")
       					   )
       					diff += 30000;
    					
    					else if ( attr.getValue().equalsIgnoreCase("large") && 
          						 attributeTableObj1.get("size").equalsIgnoreCase("medium")
          					   )
          				diff += 40000;
    					
    					else if ( attr.getValue().equalsIgnoreCase("small") && 
         						 attributeTableObj1.get("size").equalsIgnoreCase("large")
         					   )
         				diff += 50000;
      				
    					else if ( attr.getValue().equalsIgnoreCase("large") && 
        						 attributeTableObj1.get("size").equalsIgnoreCase("small")
        					   )
        				diff += 60000;
     				
       				
    				}
    			}
    			else // unlikely but possible : no shape 
    			{
    				
    			}
    		}
    		
    		if (attr.getName().equalsIgnoreCase("angle"))
    		{
    			if (attributeTableObj1.containsKey("angle"))
    			{
    				int angle2 = Integer.parseInt(attr.getValue());
    				int angle1 = Integer.parseInt(attributeTableObj1.get("angle"));
    				if (attr.getValue().equalsIgnoreCase(attributeTableObj1.get("angle")))
    				{
    					// No attribute difference is also encoded
    					// if obj2.
    					if (bothCircles == 1 && diffTable == diffTableCD)
    					{
    						// look up the diffTableAB for angle change and add the angle changes blindly for circles
    						 Collection<Integer> values = diffTableAB.values();
    						 
    						 Iterator<Integer> valueIter = values.iterator();
    						 int value = 0;
    						 while (valueIter.hasNext())
    						 {
    							 value = valueIter.next();
    							 value = value % 1000;
    							 if (value > 900 && value < 1359 && value != 1001 && value != 999 && value != 1001 && value!= 1002
    									 && value != 1005 && value!= 1010 && value != 1015)
    							 {
    								 // diffTable.put(obj1, new Integer(value));
    								 diff += value;
    							 }
    						 }
    					}
    					// else diff = 999;
    				}
    				else // could be encoded to more detailed level
    				{
    					// if (angle)
    					// diff += 1000;
    					int diff_temp = diff;
    					diff = 1000 + (angle2 - angle1);

    					// check for reflection around 'y'
    					if ((angle1 == 180 && angle2 == 90) || 
    					    (angle1 == 90 && angle2 == 180)  ||
    					    (angle1 == 270 && angle2 == 0)||
    					    (angle1 == 0 && angle2 == 270))
    					{
    						diff = diff_temp;
							diff += 1015;
    						
    					}
    					
    					
    					// check for reflection around 'x'
    					else if ((angle1 == 180 && angle2 == 270) || 
    					    (angle1 == 90 && angle2 == 0)  ||
    					    (angle1 == 270 && angle2 == 180)||
    					    (angle1 == 0 && angle2 == 90))
    					{
    						diff = diff_temp;
							diff += 1010;
    						
    					}
    					
    					
    					// check for clockwise rotation
    					else if ((angle1 == 180 && angle2 == 90) || 
    					    (angle1 == 0 && angle2 == 270)  ||
    					    (angle1 == 270 && angle2 == 180)||
    					    (angle1 == 90 && angle2 == 180))
    					{
    						diff = diff_temp;
							diff += 1005;
    						
    					}
    					
    					
    					else 
    					{
	    					if (angle1 < 90) // first quadrant
	    					{
	    						if (angle2 - angle1 == 90) // reflection around Y axis
	    						{
	    							
	    							diff = diff_temp;
	    							diff += 1001;
	    						}
	    					}
	    					
	    					else if (angle1 > 270 && angle1 < 360)
	    					{
	    						if (angle1 - angle2 == 90)
	    						{
	    							diff = diff_temp;
	    							diff += 1001;
	    						}
	    					}
	    					
	    					else if (angle1 > 90 && angle1 <180)
	    					{
	    						if (angle1 - angle2 == 90) // reverse reflection around y axis
	    						{
	    							diff = diff_temp;
	    							diff += 1002;
	    						}
	    					}
	    					
	    					else if (angle1 > 180 && angle2 < 270)
	    					{
	    						if (angle2 - angle1 == 90) // reflection around Y axis
	    						{
	    							diff = diff_temp;
	    							diff += 1002;
	    						}
	    					}
    					}
    					
    					
    				}
    			}
    			else // unlikely but possible : no angle 
    			{
    				
    			}
    		}
    		
    		// encoding fill attribute changes
    		if (attr.getName().equalsIgnoreCase("fill"))
    		{
    			if (attributeTableObj1.containsKey("fill"))
    			{
    				if (attr.getValue().equalsIgnoreCase(attributeTableObj1.get("fill")))
    				{
    					// No fill difference
    					// diff += 4999;
    				}
    				
    				// if changed from 'yes' to 'no'
    				else if ( attr.getValue().equalsIgnoreCase("yes") && 
       						 attributeTableObj1.get("fill").equalsIgnoreCase("no")
       					   )
    						diff += 5000;
    				
    				else if ( attr.getValue().equalsIgnoreCase("no") && 
      						 attributeTableObj1.get("fill").equalsIgnoreCase("yes")
      					   )
   						diff += 5500;
    				
    				else if ( attr.getValue().equalsIgnoreCase("no") && 
     						 attributeTableObj1.get("fill").equalsIgnoreCase("left-half")
     					   )
  						diff += 5015;

    				
    				else if ( attr.getValue().equalsIgnoreCase("left-half") && 
    						 attributeTableObj1.get("fill").equalsIgnoreCase("no")
    					   )
 						diff += 5030;
    				
    				else if ( attr.getValue().equalsIgnoreCase("no") && 
    						 attributeTableObj1.get("fill").equalsIgnoreCase("right-half")
    					   )
 						diff += 5045;
    				
    				else if ( attr.getValue().equalsIgnoreCase("right-half") && 
    						 attributeTableObj1.get("fill").equalsIgnoreCase("no")
    					   )
 						diff += 5060;

    				else if ( attr.getValue().equalsIgnoreCase("no") && 
    						 attributeTableObj1.get("fill").equalsIgnoreCase("top-half")
    					   )
 						diff += 5075;
    				
    				else if ( attr.getValue().equalsIgnoreCase("no") && 
   						 attributeTableObj1.get("fill").equalsIgnoreCase("upper-half")
   					   )
						diff += 5075;
   				

    				else if ( attr.getValue().equalsIgnoreCase("top-half") && 
   						 attributeTableObj1.get("fill").equalsIgnoreCase("no")
   					   )
						diff += 5090;
   				
   				    else if ( attr.getValue().equalsIgnoreCase("upper-half") && 
  						 attributeTableObj1.get("fill").equalsIgnoreCase("no")
  					   )
						diff += 5090;

    				
    				else if ( attr.getValue().equalsIgnoreCase("no") && 
    						 attributeTableObj1.get("fill").equalsIgnoreCase("bottom-half")
    					   )
 						diff += 5105;

    				else if ( attr.getValue().equalsIgnoreCase("no") && 
   						 attributeTableObj1.get("fill").equalsIgnoreCase("lower-half")
   					   )
						diff += 5105;

    				
    				else if ( attr.getValue().equalsIgnoreCase("bottom-half") && 
   						 attributeTableObj1.get("fill").equalsIgnoreCase("no")
   					   )
						diff += 5120;

   				    else if ( attr.getValue().equalsIgnoreCase("lower-half") && 
  						 attributeTableObj1.get("fill").equalsIgnoreCase("no")
  					   )
						diff += 5120;

    				else if ( attr.getValue().equalsIgnoreCase("no") && 
   						 attributeTableObj1.get("fill").equalsIgnoreCase("top-left,bottom-left") // same case
   					   )
						diff += 5135;
    				
    				else if ( attr.getValue().equalsIgnoreCase("top-left,top-right") && 
      						 attributeTableObj1.get("fill").equalsIgnoreCase("top-left,top-right,bottom-left") // same case
      					   )
   						diff += 5135;
    				
    				else if ( attr.getValue().equalsIgnoreCase("no") && 
      						 attributeTableObj1.get("fill").equalsIgnoreCase("bottom-left,top-left") // same case
      					   )
   						diff += 5135;

    				else if ( attr.getValue().equalsIgnoreCase("no") && 
     						 attributeTableObj1.get("fill").equalsIgnoreCase("bottom-left,top-left") // same case
     					   )
  						diff += 5135;

    				
    				
    				else if ( attr.getValue().equalsIgnoreCase("top-left,bottom-left") && 
      						 attributeTableObj1.get("fill").equalsIgnoreCase("no")
      					   )
   						diff += 5150;
    				
    				else if ( attr.getValue().equalsIgnoreCase("top-left,top-right,bottom-left") && 
     						 attributeTableObj1.get("fill").equalsIgnoreCase("top-left,top-right")
     					   )
  						diff += 5150;
   				
    				
    				else if ( attr.getValue().equalsIgnoreCase("bottom-left,top-left") && 
     						 attributeTableObj1.get("fill").equalsIgnoreCase("no")
     					   )
  						diff += 5150;

    				else if ( attr.getValue().equalsIgnoreCase("bottom-left,top-right,top-left") && 
    						 attributeTableObj1.get("fill").equalsIgnoreCase("bottom-left,top-right")
    					   )
 						diff += 5150;


    			}
    			else // unlikely but possible : no fill change 
    			{
    				
    			}
    		}
    	
    		
    		// assuming if one has positional then another has
    		if (positionalAttrs.contains(attr.getName().toLowerCase()))
    		{
    			String value_A = null, value_B = null;
    			value_B = attr.getValue();
				  
				  if (attributeTable.containsKey(attr.getName().toLowerCase()))
				  {
					  
					  value_A = attributeTable.get(attr.getName().toLowerCase());
			  
					  if (value_A.contentEquals(value_B) || (value_A.length() == value_B.length()))
					  {
						  diff += 499;// nothing to do 
					  }
					  else
					  {
						  diff += 500;
					  }
				  }
				  
				  else 
				  {
					  diff += 600;
				  }
				
    		}
    		// else diff -= 1;
    		
    	} // iteration over attribs of obj2 complete
    	
    	diffTable.put(obj1, new Integer(diff));
    }
    
    // obj : Object from fig. 'A'
    // This function will find corresponding object from fig B and return its Index
    // That index will then be used to calculate the difference between A and B.
    
    private RavensObject findCorrespondingObjectAB(
    												RavensObject obj,
    												RavensProblem problem, 
    												ArrayList<RavensObject> objectsFound,
    												String figure
    											  )
    {
    	
    	  attributeTable = new HashMap<String,String>();
    	  HashMap<String,RavensFigure> allfigures = problem.getFigures();
    	  // 
    	  // extract attributes here
		  ArrayList<RavensAttribute> attributes = obj.getAttributes();
		  
		  ListIterator <RavensAttribute> attrIterator = attributes.listIterator();
		  
		  RavensAttribute attr;
		  int numberOfAttributes = 0;
		  
		  // add all attributes of object from fig A to attribute table
		  // we will use this to compare attributes from fig B
		  while(attrIterator.hasNext())
		  {
			  attr = attrIterator.next();
			  attributeTable.put(attr.getName().toLowerCase(),attr.getValue()); // attribs : all lower case
			  numberOfAttributes++;
			  // System.out.println(attr.getName() + "  " + attr.getValue());
		  }
		  
		  
		  if(allfigures.containsKey(figure))
	      {
	    		ArrayList<RavensObject> objects = allfigures.get(figure).getObjects();
	    		ListIterator <RavensObject> ObjectsIterator = objects.listIterator();
	    		
	    		RavensObject obj_correponding;
	    		
	    		while(ObjectsIterator.hasNext()) // for each object in 'B'
	    		{
	    			
	    			  // skip the objects
	    			
	    			  obj_correponding = ObjectsIterator.next();
	    			  
	    			  if(objectsFound.contains(obj_correponding))
	    			  {
	    				  continue;
	    			  }
	    			  
	    			  // System.out.println(obj_correponding.getName());
	    			  
	    			  // attributes.clear();
	    			  
	    			  // extract attributes here
	    			  attributes = obj_correponding.getAttributes();
	    			  
	    			  // attrIterator.
	    			  attrIterator = attributes.listIterator();
	    			  
	    			  // attr
	    			  
	    			  boolean isSameShape   	= false;
	    			  boolean bothHaveShape 	= false, noOneHasShape = true;
	    			  boolean isSameSize    	= false;
	    			  boolean bothHaveSize 		= false, noOneHasSize  = true;
	    			  boolean isSameFill 		= false;
	    			  boolean bothHaveFill		= false, noOneHasFill  = true;
	    			  boolean bothHaveAngle 	= false, noOneHasAngle = true;
	    			  boolean isSameAngle 		= false;
	    			  boolean bothHavePos 		= false, noOneHasPos   = true;
	    			  boolean isSamePos			= false;
	    			  
	    			  
	    			  // Checking which all attribs object from fig A 
	    			  if (attributeTable.containsKey("angle"))
					  {
	    				  noOneHasAngle = false;
					  }
	    			  
	    			  if (attributeTable.containsKey("size"))
					  {
	    				  noOneHasSize = false;
					  }
	    			  
	    			  if (attributeTable.containsKey("shape"))
					  {
	    				  noOneHasShape = false;
					  }
	    			  
	    			  if (attributeTable.containsKey("fill"))
					  {
	    				  noOneHasFill = false;
					  }
	    			  
	    			  Iterator<String> posIterator = positionalAttrs.iterator();
					  while(posIterator.hasNext())
					  {
						  
						  if (attributeTable.containsKey(posIterator.next()))
						  {
							  noOneHasPos = false;
							  // System.out.println("both have pos!");
						  }
					  }
	    			  // check complete for object from fig A
					  
	    			  while(attrIterator.hasNext()) // find out the attribute relations and after the loop make the decision
	    			  {
	    				  String value_A = null, value_B = null;
	    				  attr = attrIterator.next();
	    				  
	    				  
	    				  if (isSameShape && isSameSize)
	    				  {
	    					  break;
	    				  }

	    				  // check if attribute is a positional attribute
	    				  if (positionalAttrs.contains(attr.getName().toLowerCase()))
	    				  {
	    					  noOneHasPos = false;
	    					  value_B = attr.getValue();
	    					  
	    					  if (attributeTable.containsKey(attr.getName().toLowerCase()))
    						  {
    							  bothHavePos = true;
    							  
    							  value_A = attributeTable.get(attr.getName().toLowerCase());
    					  
								  if (value_A.contentEquals(value_B))
								  {
									  isSamePos = true;
									  // System.out.println("Same pos!");	  
								  }
    						  }
	    					  
	    					  
	    					  else 
	    					  {
	    						   posIterator = positionalAttrs.iterator();
	    						  
	    						  while(posIterator.hasNext())
	    						  {
	    							  
	    							  if (attributeTable.containsKey(posIterator.next()))
	    							  {
	    								  bothHavePos = true;
	    								  // System.out.println("both have pos!");
	    							  }
	    						  }
	    					  }
    						  
    						  continue;
	    				  }
	    					
	    				  
	    				  if (attr.getName().equalsIgnoreCase("angle"))
	    				  {
	    					  noOneHasAngle = false;
	    					  value_B = attr.getValue();
	    					  
    						  if (attributeTable.containsKey("angle"))
    						  {
    							  bothHaveAngle = true;
    							  
    							  value_A = attributeTable.get("angle");
    					  
								  if (value_A.contentEquals(value_B))
								  {
									  isSameAngle = true;
									  // System.out.println("Same angle!");	  
								  }
    						  }
    						  
    						  continue;

	    				  }

	    				  if (attr.getName().equalsIgnoreCase("fill"))
	    				  {
	    					  noOneHasFill = false;
	    					  value_B = attr.getValue();
	    					  
    						  if (attributeTable.containsKey("fill"))
    						  {
    							  bothHaveFill = true;
    							  
    							  value_A = attributeTable.get("fill");
    					  
								  if (value_A.contentEquals(value_B))
								  {
									  isSameFill = true;
									  // System.out.println("Same fill!");	  
								  }
    						  }
    						  
    						  continue;

	    				  }
	    				  
	    				  if (attr.getName().equalsIgnoreCase("size"))
    					  {
	    					  noOneHasSize = false;
    						  value_B = attr.getValue();
    					  
    						  if (attributeTable.containsKey("size"))
    						  {
    							  bothHaveSize = true;
								  
    							  value_A = attributeTable.get("size");
    							  
    							  if (value_A.contentEquals(value_B))
								  {
									  isSameSize = true;
									  // System.out.println("Same size!");	  
								  }
    						  }
    						  
    						  continue;
    					  }
	    					 
	    				  // check if it is shape attribute, compare with the A's attribute table
	    				  if (attr.getName().equalsIgnoreCase("shape"))
	    				  {
	    					  noOneHasShape = false;
	    					  value_B = attr.getValue();
	    					  
	    					  // remember we formed attribute table for A's object
	    					  if (attributeTable.containsKey("shape"))
    						  {
	    						  bothHaveShape = true;
	    						  
    							  value_A = attributeTable.get("shape");
    					  
								  if (value_A.contentEquals(value_B))
								  {
									  isSameShape = true;
									  // System.out.println("Same shape!");	  
								  }
    						  }
    						  
	    					  continue;
	    				  }
	    				  
	    			  } // end of attribute iteration loop 
	    			  
	    			  
	    			  
	    			  if (isSameShape && isSameSize)
	    			  {
	    				  // System.out.println("Yippie-SameShapeAndSize");
	    				  return obj_correponding;  
	    			  }
	    			  
	    			  if ( isSameShape    && 
	    				   (noOneHasSize  || bothHaveSize)  &&
	    				   (noOneHasAngle || bothHaveAngle) &&
	    				   (noOneHasFill  || bothHaveFill)  &&
	    				   (noOneHasPos   || bothHavePos)
	    				 )
	    			  {
	    				  // implies shape is same and all other attributes are common
	    				  // System.out.println("Yippie");
	    				  return obj_correponding;
	    			  }
	    			  
	    			  if (!isSameShape)
	    			  {
	    				  if ((noOneHasSize  ||  isSameSize)  &&
	    				     (noOneHasAngle || isSameAngle) &&
	    				     (noOneHasFill  ||  isSameFill)  &&
	    				     (noOneHasPos   ||  isSamePos))
	    				     {
	    					  	// System.out.println("Yippie-onlyShapeChange");
	    					  	return obj_correponding;
	    				     }
	    			  }
	    			  
	    			  /*
	    			  if ( !isSameShape    && 
		    				   (noOneHasSize  || bothHaveSize)  &&
		    				   (noOneHasAngle || bothHaveAngle) &&
		    				   (noOneHasFill  || bothHaveFill)  &&
		    				   (noOneHasPos   || bothHavePos)
		    				 )
		    			  {
		    				  // implies shape is same and all other attributes are common
		    				  System.out.println("Yippie-onlyShapeChange");
		    			  }
		    			*/  
	    			  // check for equality of all attribs except size
	    			  /*if (isSameShape && isSameFill && isSameAngle && isSamePos)
	    			  {
	    				  return obj_correponding;
	    			  }*/
	    			  
	    			  // check for equality of all but position attribs
	    			  /*if (isSameFill && isSameAngle && !isSamePos)
	    			  {
	    				  return obj_correponding;
	    			  }*/
	    		
	    			  // System.out.println("No Match found!!");
	    		} // end of objects iteration loop  
	      
	      } // if(allfigures.containsKey("B")) 

	    	
		// System.out.println("No Match found!!");
		return null;
    	
    }
    
    private void addDiffToTable(RavensProblem problem, String fig1, String fig2, HashMap<RavensObject, Integer> diffTable)
    {
    	
    	int exchanged = 0;
    	HashMap<String,RavensFigure> allfigures = problem.getFigures();
    	
    	// check if fig2 has more objects than fig1. If yes, swap them
    	ArrayList<RavensObject> objectsFig1 = allfigures.get(fig1).getObjects();
    	ArrayList<RavensObject> objectsFig2 = allfigures.get(fig2).getObjects();
		if (objectsFig1.size() < objectsFig2.size())
		{
			exchanged = 1;
			// System.out.println(fig1+ "," +fig2);
			
			String temp = fig1;
			fig1 = fig2;
			fig2 = temp;
			
			// System.out.println(fig1+ "," +fig2);
		}
    	
    	ArrayList<RavensObject> objects = allfigures.get(fig1).getObjects();
    	
    	ListIterator <RavensObject> ObjectsIterator = objects.listIterator();
		
		
		RavensObject obj;
		
		Integer objIndex  = new Integer(1);
		Integer objIndexB = new Integer(0);
		RavensObject obj_corresponding;
		
		ArrayList<RavensObject> objectsFound = new ArrayList<RavensObject>();
		// objectsFound.`
		
		while(ObjectsIterator.hasNext())
		{
			  obj = ObjectsIterator.next();
			  
			  // System.out.println(objIndex);
			  
			  // extract attributes here
			  ArrayList<RavensAttribute> attributes = obj.getAttributes();
			  
			  ListIterator <RavensAttribute> attrIterator = attributes.listIterator();
			  
			  RavensAttribute attr;
			  int numberOfAttributes = 0;
			  
			  while(attrIterator.hasNext())
			  {
				  attr = attrIterator.next();
				  addAttributeDiff(fig1 + objIndex.toString() + attr.getName(),attr.getValue());
				  numberOfAttributes++;
				  // System.out.println(attr.getName() + "  " + attr.getValue());
			  }
			  
			  // At this point attribute table for a object in 'A' is formed.
			  // Now we find corresponding object in 'B'
			  
			  obj_corresponding = findCorrespondingObjectAB(obj, problem, objectsFound,fig2); // "B"
			  objectsFound.add(obj_corresponding);
			  fillupDiffTable(diffTable, obj, obj_corresponding, exchanged);
			  objIndex += 1;
			  
		} // while(ObjectsIterator.hasNext()) : loop over all objects from fig A

    }
    
    private void analyzeDiffTables(HashMap<RavensObject,Integer> diffTableAB, HashMap<RavensObject,Integer> diffTableCD, int choice, int cross)
    {
    	// choiceMatchArray[i]
    	// get the difference between valueSet of AB and CD and add number of matches to 
    	
    	Set<RavensObject> objects = diffTableAB.keySet();
    	
    	RavensObject obj; 
    	
    	Iterator<RavensObject> tableIter = objects.iterator();
    	
    	Integer difference, value;
    	Set<RavensObject> keys = diffTableCD.keySet();
    	int found = 0;
    	while (tableIter.hasNext())
    	{
    		found = 0;
    		obj        = tableIter.next();
    		difference = diffTableAB.get(obj);
    		
    		Iterator<RavensObject> iterator = keys.iterator();
    		
    		while (iterator.hasNext())
    		{
    			
    			RavensObject key = iterator.next();
    			value = diffTableCD.get(key);
    		
    			if (value.equals(difference))
	    		{
	    			if (cross == 0)choiceMatchArray[choice - 1] += 1; // to accomodate for indexing start from 0
	    			else choiceMatchCross[choice - 1] += 1;
	    			// diffTableCD.put(key, value);
	    			keys.remove(key);
	    			found = 1;
	    			break;
	    			// 
	    		}
	    		
    		}
    		
    		if(found == 0){
    			if (cross == 0)choiceMatchArray[choice - 1] -= 1; // to accomodate for indexing start from 0
    			else choiceMatchCross[choice - 1] -= 1;
    		}
	    		
    	}
    	// Iterator tableIter = diffTableAB.entrySet().iterator();
    	
    	// diffTableAB.containsValue(value)
    }
    
    private void parse_2byx(RavensProblem problem) {
    	
    	RavensProblem trial_problem = problem;
    	
    	HashMap<String,RavensFigure> allfigures = problem.getFigures();
    	
    	Set keys = allfigures.keySet();
    	
    	
    	problemMap = new HashMap <String,RavensFigure>();
    	
    	if(allfigures.containsKey("A"))
    	{
    		addDiffToTable(problem, "A", "B", diffTableAB);
    		   		
    	} // if(allfigures.containsKey("A"))
		

    	if(allfigures.containsKey("C")) // originally "C" 
    	{
    			// loop over all choices
    			for (int i = 1; i <= 6; i++)
    			{
    					diffTableCD.clear();
    					addDiffToTable(problem, "C", new Integer(i).toString(), diffTableCD);
    					analyzeDiffTables(diffTableAB, diffTableCD, i,0);
    					
					   
				} // end of for loop : over all choices  
    	
    	} // if(allfigures.containsKey("C"))

    	
    	if(allfigures.containsKey("A"))
    	{
    		diffTableAB.clear();
    		addDiffToTable(problem, "A", "C", diffTableAB);
    		   		
    	} // if(allfigures.containsKey("A"))
		

    	if(allfigures.containsKey("B")) // originally "C" 
    	{
    			// loop over all choices
    			for (int i = 1; i <= 6; i++)
    			{
    					diffTableCD.clear();
    					addDiffToTable(problem, "C", new Integer(i).toString(), diffTableCD);
    					analyzeDiffTables(diffTableAB, diffTableCD, i,1);
    					
					   
				} // end of for loop : over all choices  
    	
    	} // if(allfigures.containsKey("C"))

    }

    
    
    
    
	
	/*-----------------------------------------------------------------------------------------------------*/
    public Agent() {
        
    }
    
    
    
    /*---------------------------- General purpose functions ------------------*/
    
    
    private int getShapeEncoding(String shape)
    {
    	String [] shapes = {"plus", "circle","righttriangle", "triangle","square","pentagon","hexagon","septagon","octagon","nonegon","decagon","pacman","dodecagon"};
    	
    	for (int i = 0; i < shapes.length; i++)
    	{
    		if (shape.equalsIgnoreCase(shapes[i]))
    		{
    			return i;
    		}
    	}
    	return -1;
    }
    
    
    private int getSizeEncoding(String size)
    {
    	String [] sizes = {"VerySmall","Small","medium","large","veryLarge"};
    	
    	for (int i = 0; i < sizes.length; i++)
    	{
    		if (size.equalsIgnoreCase(sizes[i]))
    		{
    			return i;
    		}
    	}
    	return -1;
    }
    

    private int getFillEncoding(String fill)
    {
    	String [] fillValues = {"yes","no","lefthalf","righthalf","quarter","upperhalf","lowerhalf"};
    	
    	for (int i = 0; i < fillValues.length; i++)
    	{
    		if (fill.equalsIgnoreCase(fillValues[i]))
    		{
    			return i;
    		}
    	}
    	return -1;
    }

    private int getAngleEncoding(String angle)
    {
    	return Integer.valueOf(angle).intValue();
    }

    /* ------------------------------------------------------------------------*/
    
    
    
    private boolean isStructuralAttrib(String attrName)
    {
    	int i = 0;
    	for (i = 0; i < positions.length; i++)
    	{
    		if(attrName.equalsIgnoreCase(positions[i]))
    		{
    			return true;
    		}
    	}
    	return false;
    }
    
    private boolean isFigure (String name)
    {
    	for (int i = 0; i < figures.length; i++)
    	{
    		if (name.equalsIgnoreCase(figures[i]))
    		{
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    private int toChoice(String currentFigName)
    {
    	for (int i = 0; i < choices.length; i++)
    	{
    		if (currentFigName.equalsIgnoreCase(choices[i]))
    		{
    			return i;
    		}
    	}
    	
    	return -1;
    }
    
    boolean checkForAllThrees(HashMap<String, Integer> tempObjCountMap)
    {
    	
    	Collection<Integer> counts = tempObjCountMap.values();
    	
    	Iterator<Integer> countItr = counts.iterator();
    	
    	while (countItr.hasNext())
    	{
    		if (countItr.next().intValue() != 3)
    		{
    			return false;
    		}
    	}
    	return true;
    }


    
    
    // we are creating attributeCountMap table here which keeps track of number of unique attributes
    // - useful for our 3-3-2 strategy and ?
    public void incrementAttributeCount(String attrId)
    {
    	if (attributeCountMap.containsKey(attrId))
    	{
    		Integer count = attributeCountMap.get(attrId);
    		attributeCountMap.remove(attrId);
    		
    		attributeCountMap.put(attrId, new Integer(count + 1));
    	}
    	else 
    	{
    		attributeCountMap.put(attrId, new Integer(1));
    	}
    }
    
    
    // check if this choice fig is present in question's fig (A to H) as well
    public boolean isFigureAlreadyPresent(String choiceFigId)
    {
    	if (figureCountMap.containsKey(choiceFigId)) 
    	{
    		return true;
    	}
    	return false;
    }
    
    
    // we are creating figureCountMap table here which keeps track of number of unique figures
    // - useful for our 3-3-2 strategy
    public void incrementFigureCount(String figId)
    {
    	if (figureCountMap.containsKey(figId))
    	{
    		Integer count = figureCountMap.get(figId);
    		figureCountMap.remove(figId);
    		
    		figureCountMap.put(figId, new Integer(count + 1));
    	}
    	else 
    	{
    		figureCountMap.put(figId, new Integer(1));
    	}
    }

    
    // we are constructing an entity count table here which keeps track of number of unique entities
    // in figures - useful for our 3-3-2 strategy
    public void incrementTempEntityCount(String objId, HashMap <String, Integer> tempEntityCountMap)
    {
    	// if (tempObjCountMap != null)
    	if ((tempEntityCountMap != null) && tempEntityCountMap.containsKey(objId))
    	{
    		Integer count = tempEntityCountMap.get(objId);
    		tempEntityCountMap.remove(objId);
    		
    		tempEntityCountMap.put(objId, new Integer(count + 1));
    	}
    	else 
    	{
    		tempEntityCountMap.put(objId, new Integer(1));
    	}
    }
    
    
    // we are creating objectCountMap table here which keeps track of number of unique objects
    // in figures - useful for our 3-3-2 strategy
    public void incrementObjectCount(String objId)
    {
    	if (objectCountMap.containsKey(objId))
    	{
    		Integer count = objectCountMap.get(objId);
    		objectCountMap.remove(objId);
    		
    		objectCountMap.put(objId, new Integer(count + 1));
    	}
    	else 
    	{
    		objectCountMap.put(objId, new Integer(1));
    	}
    }
    
    
    private boolean suitableFor_3_3_2_attrCountBased()
    {
    	// check if the value set from objectCountMap is all 2's and 3's
    	
    	Collection <Integer> attrCounts = attributeCountMap.values();
    	
    	Iterator <Integer> countItr = attrCounts.iterator();

    	int value = 0;
    	while (countItr.hasNext())
    	{
    		value = countItr.next().intValue(); 
    		if ((value != 3) && (value != 2))
    			return false;
    	}
    	return true;
    }

    
    
    private boolean suitableFor_3_3_2_objectCountBased()
    {
    	// check if the value set from objectCountMap is all 2's and 3's
    	
    	Collection <Integer> objCounts = objectCountMap.values();
    	
    	Iterator <Integer> countItr = objCounts.iterator();
    	int value = 0;
    	while (countItr.hasNext())
    	{
    		value = countItr.next().intValue(); 
    		if ((value != 3) && (value != 2))
    			return false;
    	}
    	return true;
    }

    
    private boolean suitableFor_3_3_2_figCountBased()
    {
    	// check if the value set from objectCountMap is all 2's and 3's
    	
    	Collection <Integer> figCounts = figureCountMap.values();
    	
    	Iterator <Integer> countItr = figCounts.iterator();
    	int value = 0;
    	while (countItr.hasNext())
    	{
    		value = countItr.next().intValue(); 
    		if ((value != 3) && (value != 2))
    			return false;
    	}
    	return true;
    }
    
    public void iterateOverFigures(RavensProblem problem, boolean overFigures, int strategy)
    {
    	HashMap <String, RavensFigure> figures = problem.getFigures();
    	
    	Set<String> figNames = figures.keySet();
    	
    	Iterator<String> figItr = figNames.iterator();
    	
    	
    	String currentFigName = "";
    	ArrayList <RavensObject> currentFigObjects;
    	RavensFigure currentFig;
    	
    	// variables to determine the category of the problem. Make sure we are Not iterating over choices 
    	// by checking overFigures boolean value
    	if (overFigures) oneObjectProblem     = true; 
    	if (overFigures) figurePatternProblem = true;
    	
    	// iterating over all figures(A,B,C,D,E,F) and form the required data structures.
    	// if overFigures is false then we are iterating over choices
    	while (figItr.hasNext())
    	{
    		
    		// for each choice, we start with original objCountMap copied into temp Map
    		HashMap <String, Integer> tempObjCountMap = null;
    		if (!overFigures)
    		{
    			if (strategy == 1)
    			{
    				tempObjCountMap = new HashMap <String, Integer>();
    				tempObjCountMap.putAll(objectCountMap);
    			}
    		}
        	
    		
    		// for each choice, we start with original figCountMap copied into tempMap
    		HashMap <String, Integer> tempFigCountMap = null;
    		if (!overFigures)
    		{
    			if (strategy == 2)
    			{
    				tempFigCountMap = new HashMap <String, Integer>();
    				tempFigCountMap.putAll(figureCountMap);
    			}
    		}
        	
    		
    		// for each choice, we start with original attributeCountMap copied into tempMap
    		HashMap <String, Integer> tempAttrCountMap = null;
    		if (!overFigures)
    		{
    			if (strategy == 3)
    			{
    				tempAttrCountMap = new HashMap <String, Integer>();
    				tempAttrCountMap.putAll(attributeCountMap);
    			}
    		}
        	
    		
    		currentFigName = figItr.next();
    		
    		if (overFigures && (!isFigure(currentFigName)))
    				continue;
    		
    		if ((!overFigures) && (isFigure(currentFigName)))
    				continue;
    		
    		
    		currentFig = figures.get(currentFigName);
    		
    		currentFigObjects = currentFig.getObjects();
    		
    		if (currentFigObjects.size() != 1) oneObjectProblem = false;
    		
    		Iterator <RavensObject> objItr = currentFigObjects.iterator();
   
    		
    		// create indices for ravens objects to be used for replacing object name in inside/above attribs with index
    		// in structural attributes
    		HashMap <String, Integer> indexOf = new HashMap<String, Integer>();
    		
    		// iterating over all objects in a given figure/choice
    		int i = 0;
    		
    		while (objItr.hasNext())
    		{
    			indexOf.put(new String (objItr.next().getName()), new Integer(i));
    			i = i + 1; 
    		}
    		
    		objItr = currentFigObjects.iterator();
    		
    		RavensObject obj;
    		
    		// figId encodes complete figure with all the objects and their attributes
    		String figId = "";
    		String prevwithoutStructObjId = "";
    		String withoutStructObjId = "";
    		
    		int objIndex = 0;
    		boolean firstObject = true;
    		// 2nd pass over all objects in a given figure/choice
    		while (objItr.hasNext())
    		{
    			//objItr.next().getName()
    			obj = objItr.next();
    			
    			if (objIndex == 0) 
    			{
    				firstObject = true;
    			}
    			objIndex++;
    			
    			ArrayList <RavensAttribute> objAttributes = obj.getAttributes();
    			
    			Iterator<RavensAttribute> attrItr = objAttributes.iterator();
    			
    			RavensAttribute attribute;
    			String attrName, attrValue;
    			
    			String objId = "";
    			
    			// iterating over all attributes of an object
    			// it is here we are in a position to identify unique attribs and hence identify the objects completely.
    			// the identification(ID) signature is attribNames+Values
    			// this ID is added to objectCountMap to keep track of number of occurences
    			
    			while (attrItr.hasNext())
    			{
    				String attrId = "";
    				
    				attribute = attrItr.next();
    				attrName = attribute.getName();
    				attrValue = attribute.getValue();
    				attrValue = attrValue.replaceAll("[-]", "");
    				String [] objectsForStructAttr; // used to retrieve the index -> inside P, Q => inside 0, 1
    				
    				
    				if (!isStructuralAttrib(attrName))
    				{
    					attrId = attrId.concat(attrName + "-" + attrValue);
    					objId  = objId.concat(attrName + "-" + attrValue);
    					
    					withoutStructObjId = withoutStructObjId.concat(attrName + "-" + attrValue);
    				}
    				
    				
    				if (isStructuralAttrib(attrName))
    				{
    					// System.out.println(attrName + "  "+attrValue);
    					objectsForStructAttr = attrValue.split(",");
    					
    					for(int j = 0; j < objectsForStructAttr.length; j++)
    					{
    						attrId = attrId.concat(attrName + "-" + indexOf.get(objectsForStructAttr[j]));
    						objId  = objId.concat(attrName + "-" + indexOf.get(objectsForStructAttr[j]));
    						// System.out.println(attrName + "  "+ indexOf.get(objectsForStructAttr[j]));
    					}
    				}
    				
    				// add the attrName and Value to attributeCount Table if we are iterating over figures
    				// if we are iterating over choices then add-up the values
    				if (overFigures)
        			{
    					incrementAttributeCount(attrId);
        				// System.out.println(objId);
        			}
        			
        			if ((!overFigures) && strategy == 3)
        			{
        				// add the objId to tempObjCountMap
        				incrementTempEntityCount(attrId, tempAttrCountMap);
        			}
        			
    			}
    			
    			if (firstObject) 
    			{
    				prevwithoutStructObjId = withoutStructObjId;
    				firstObject = false;
    			}
    			if (!withoutStructObjId.equalsIgnoreCase(prevwithoutStructObjId)) 
    				figurePatternProblem = false;
    			
    			prevwithoutStructObjId = withoutStructObjId;
    			withoutStructObjId     = "";
    			
    			
    			// if we are iterating over figures then increment the counter for this objectID
    			// in objectCountMap
    			if (overFigures)
    			{
    				incrementObjectCount(objId);
    				// System.out.println(objId);
    			}
    			
    			if ((!overFigures) && strategy == 1)
    			{
    				// add the objId to tempObjCountMap
    				incrementTempEntityCount(objId, tempObjCountMap);
    			}
    			
    			figId = figId.concat(objId);
    			
    		} // end of iteration over a figure / a choice
    		
    		
    		// if we are iterating over figures then increment the counter for this figureID
			// in figureCountMap - basically a table of each figure's count
			if (overFigures)
			{
				incrementFigureCount(figId);
				// System.out.println(objId);
			}
			
			// for each choice, construct temp figure count table to see if it completes 3-3-3 
			if ((!overFigures) && strategy == 2)
			{
				// add the figId to tempFigCountMap
				incrementTempEntityCount(figId, tempFigCountMap);
			}
			
			// if (strategy == )
			
    		// System.out.println("FigID - " + figId);
    		
    		// check the tempObjCountMap if it has got all 3's now
    		if ((!overFigures) && strategy == 1)
    		{
    			if (checkForAllThrees(tempObjCountMap) == true)
    			{
    				int index = toChoice(currentFigName);
    				
    				if (index != -1)
    					probableChoicesStrategy_1[index] = 1;
    			}
    		}
    		
    	
    		// check the tempObjCountMap if it has got all 3's now
    		if ((!overFigures) && strategy == 2)
    		{
    			if (checkForAllThrees(tempFigCountMap) == true)
    			{
    				int index = toChoice(currentFigName);
    				
    				if (index != -1)
    					probableChoicesStrategy_1[index] = 1;
    			}
    		}

    		
    		// check the tempObjCountMap if it has got all 3's now
    		if ((!overFigures) && strategy == 3)
    		{
    			if (checkForAllThrees(tempAttrCountMap) == true)
    			{
    				int index = toChoice(currentFigName);
    				
    				if (index != -1)
    					probableChoicesStrategy_1[index] = 1;
    			}
    		}

    		// this is not so accurate but looks effective strategy
    		// making sure no Q is left un-answered !
    		if ((!overFigures) && strategy == 4)
    		{
    			int index = toChoice(currentFigName);
    			if (!isFigureAlreadyPresent(figId))
    			{
    				if(currentFigObjects.size() == figures.get("H").getObjects().size())
    				{
    					
    					if (index != -1)
    						probableChoicesStrategy_1[index] = 1;
    				}
    				else 
    				{
    					;// probableChoicesStrategy_1[index] = 1; // because 5 is my favorite choice :-)
    				}
    			}
    			
    			else
    			{
    				;//probableChoicesStrategy_1[index] = 1; // because 5 is my favorite choice :-)
    			}
    		}
    		
    		// System.out.println("CheckPoint");
    		// System.out.println("---*--");
    			
    	}
   
    }
    
    
    /* ----------------------- Pattern matching strategy --------------------*/
    
    public void constructfigurePatternTable(RavensProblem problem)
    {
        String [] fig = {"A","B","C","D","E","F","G","H"};
    	
    	HashMap <String, RavensFigure> figures = problem.getFigures();
    	
    	RavensFigure currentFig;// = figures.get(fig[0]); 
    	
    	RavensObject currentObj;// = currentFig.getObjects();
    	
    	ArrayList<RavensAttribute> attribList;
    	
    	String attrName, attrValue;
    	int numberOfObjects = -1;
    	
    	for (int i = 0; i < 8; i++)
    	{
    		currentFig = figures.get(fig[i]);
    		
    		// though this is a not figure with only one object, but all objects have same attribs
    		currentObj = currentFig.getObjects().get(0);
    	    numberOfObjects = currentFig.getObjects().size(); 	
    	    
    	    figureAttributeTable[i][4] = numberOfObjects;
    	    
    		attribList = currentObj.getAttributes();
    		
    		Iterator <RavensAttribute> attrItr = attribList.iterator();
    		
    		RavensAttribute attribute;
    		
    		while (attrItr.hasNext())
    		{
    			attribute = attrItr.next();
				attrName = attribute.getName();
				attrValue = attribute.getValue();
				attrValue = attrValue.replaceAll("[-]", "");
				
				if (attrName.equalsIgnoreCase("shape"))
				{
					figureAttributeTable[i][0] = getShapeEncoding(attrValue);
				}
				
				if (attrName.equalsIgnoreCase("size"))
				{
					figureAttributeTable[i][1] = getSizeEncoding(attrValue);
				}
				
				if (attrName.equalsIgnoreCase("fill"))
				{
					figureAttributeTable[i][2] = getFillEncoding(attrValue);
				}
				
				if (attrName.equalsIgnoreCase("angle"))
				{
					figureAttributeTable[i][3] = getAngleEncoding(attrValue);
				}
				
    		}	
    	}   	
    }

    public void findTargetChoice_PatternMatchingStrategy(RavensProblem problem, int[] targetValues)
    {
        // String [] fig = {"A","B","C","D","E","F","G","H"};
    	String [] choices = {"1","2","3","4","5","6"};
    	
    	HashMap <String, RavensFigure> figures = problem.getFigures();
    	
    	RavensFigure currentChoice;// = figures.get(fig[0]); 
    	
    	RavensObject currentObj;// = currentFig.getObjects();
    	
    	ArrayList<RavensAttribute> attribList;
    	
    	String attrName, attrValue;
    	
    	int numObj = -1;
    	int maxMatchChoice = 0, maxMatches = -1;
    	
    	for (int i = 0; i < choices.length; i++)
    	{
    		
    		int valueMatchCount = 0;
    		currentChoice = figures.get(choices[i]);
    		
    		numObj = currentChoice.getObjects().size();
    		if (targetValues[4] == numObj)
    		{
    			valueMatchCount++;
    			
    			// special case where choice has zero objects.
    			// in that case we know everything matched and we just break out of matching loop
    			if (numObj == 0)
    			{
    				valueMatchCount = 5;
    				if (valueMatchCount > maxMatches)
    		    	{
    		    	 	maxMatchChoice = i;
    		    	 	maxMatches = valueMatchCount;
    		    	}    		   
    				continue;
    				// goto update;
    			}
    		}
    		
    		// remember this is a figure with only one object
    		// if (currentChoice.getObjects().size() != 1) continue;
    		
    		if (numObj != 0)
    		{
    			currentObj = currentChoice.getObjects().get(0);
    		
    		
    			attribList = currentObj.getAttributes();
    		
    			Iterator <RavensAttribute> attrItr = attribList.iterator();
    		
	    		RavensAttribute attribute;
	    		
	    		
	    		while (attrItr.hasNext())
	    		{
	    			attribute = attrItr.next();
					attrName = attribute.getName();
					attrValue = attribute.getValue();
					attrValue = attrValue.replaceAll("[-]", "");
					
					if (attrName.equalsIgnoreCase("shape"))
					{
						if (targetValues[0] == getShapeEncoding(attrValue))
						{
							valueMatchCount++;
						}
					}
					
					if (attrName.equalsIgnoreCase("size"))
					{
						if (targetValues[1] == getSizeEncoding(attrValue))
						{
							valueMatchCount++;
						}
					}
					
					if (attrName.equalsIgnoreCase("fill"))
					{
						if (targetValues[2] == getFillEncoding(attrValue))
						{
							valueMatchCount++;
						}
					}
					
					if (attrName.equalsIgnoreCase("angle"))
					{
						if (targetValues[3] == getAngleEncoding(attrValue))
						{
							valueMatchCount++;
						}
					}			
	    		}	
    		}
    		
    		// find choice with maximum targetValues match
    		if (valueMatchCount > maxMatches)
    		{
    		 	maxMatchChoice = i;
    		 	maxMatches = valueMatchCount;
    		} 
    		 
    	}   	
    	probableChoicesStrategy_1[maxMatchChoice] = 1;
    	System.out.println(problem.getName() + "->" + (maxMatchChoice + 1));
    }
    

    
    public void findAnsChoice_PatternMatchingStrategy(RavensProblem problem)
    {
    	// First analyze the objectAttributeTable to find out the pattern of change in a row
    	
    	boolean successivePattern = true;
    	int[] attribValueDiff = new int [3]; // 0: shape, 1:size, 2:fill, 3:angle, 5: # of objects
    	int[] targetValues = new int [5];
		
    	
    	// let us run this loop for shape, size and fill only
    	for (int i = 0; i < 3; i++)
    	{
    		if ( (figureAttributeTable[2][i] - figureAttributeTable[1][i]) != 
    	    		 (figureAttributeTable[5][i] - figureAttributeTable[4][i])
    	    	)
    		{
    			successivePattern = false;
    			break;
    		}
    		attribValueDiff[i] = figureAttributeTable[2][i] - figureAttributeTable[1][i];
    	}
    	
    	// note down regular magnitude change in shape, size and fill
    	if (successivePattern)
    	{
    		targetValues[0]  = figureAttributeTable[7][0] + attribValueDiff[0];
    		targetValues[1]  = figureAttributeTable[7][1] + attribValueDiff[1];
    		targetValues[2]  = figureAttributeTable[7][2] + attribValueDiff[2];
    	}
    	
    	// need to find out angle and number of objects column relations
    	
    	// first for the angle
    	{
    		int angleA = figureAttributeTable[0][3];
    		int angleB = figureAttributeTable[1][3];
    		int angleC = figureAttributeTable[2][3];
    		
    		int angleD = figureAttributeTable[3][3];
    		int angleE = figureAttributeTable[4][3];
    		int angleF = figureAttributeTable[5][3];
    		
    		int angleG = figureAttributeTable[6][3];
    		int angleH = figureAttributeTable[7][3];
    		
    		
    		// detect if regular pattern exists
    		if ((angleC - angleB) == (angleB - angleA))
   		    {
    			targetValues[3] = (angleH + angleB - angleA) % 360;
   		    }
   		
    		// if C's angle is sum of (A +or- B )/360
    		else if ((((angleA + angleB)%360) == angleC) && (((angleD + angleE)%360) == angleF))
    		{
    			targetValues[3] = (angleG + angleH)%360; 
    		}
    		else if ((((angleA - angleB)%360) == angleC) && (((angleD - angleE)%360) == angleF))
    		{
    			targetValues[3] = (angleG - angleH)%360; 
    		}
    		
    	
    		// detect flip pattern
    		else if ((angleA == angleC) && (angleB == angleD))
    		{
    			targetValues[3] = angleG;
    		}
    		
    		
    		// Now find the pattern that exists among number of objects
    		int numObjA = figureAttributeTable[0][4];
    		int numObjB = figureAttributeTable[1][4];
    		int numObjC = figureAttributeTable[2][4];
    		
    		int numObjD = figureAttributeTable[3][4];
    		int numObjE = figureAttributeTable[4][4];
    		int numObjF = figureAttributeTable[5][4];
    		
    		int numObjG = figureAttributeTable[6][4];
    		int numObjH = figureAttributeTable[7][4];
    		
    		
    		// are the number of objects in arithmatic progression?
    		if ((numObjB - numObjA) == (numObjE - numObjD))
    		{
    			targetValues[4] = numObjH + (numObjE - numObjD);
    		}
    		
    		
    		// are the number of objects in Geometric progression?
    		else if (((numObjB/numObjA) == (numObjC/numObjB)) && ((numObjA/numObjB) == (numObjB/numObjC)))
    		{
    			System.out.println("----- ?????? Really ???????????????");
    			if ((numObjB/numObjA) > 1)
    			{
    				targetValues[4] = numObjH*(numObjH/numObjG); 
    			}
    			else
    				targetValues[4] = numObjH/(numObjG/numObjH);
    				
    		}
    		
    		
    		// are the number of objects multiples of first element (1,2,3; 2,4,6; 3,6,x)?
    		if ((1.0*numObjD/numObjA) == (1.0*numObjE/numObjB))
    		{
    			targetValues[4] = numObjG * (numObjC/ numObjA);
    		}
    		System.out.println("shape" + targetValues[0] + ";size" + targetValues[1] + 
    							";fill" + targetValues[2] + ";angle" + targetValues[3] + 
    							"#objects" + targetValues[4]);
    		
    		
    		// this function writes the correct(?) choice to a shared array.
    		findTargetChoice_PatternMatchingStrategy(problem, targetValues);
    		// findTargetChoice(problem, targetValues);
    	}
    	// System.out.println(successivePattern);
    }
    
    /*-------------------------------------------------------------------------*/
    
    /* ----------------------- One object problem strategy --------------------*/
   
    public void constructAttributeTable(RavensProblem problem)
    {
        String [] fig = {"A","B","C","D","E","F","G","H"};
    	// String [] choices = {"1","2","3","4","5","6"};
    	
    	HashMap <String, RavensFigure> figures = problem.getFigures();
    	
    	RavensFigure currentFig;// = figures.get(fig[0]); 
    	
    	RavensObject currentObj;// = currentFig.getObjects();
    	
    	ArrayList<RavensAttribute> attribList;
    	
    	String attrName, attrValue;
    	for (int i = 0; i < 8; i++)
    	{
    		currentFig = figures.get(fig[i]);
    		
    		// remember this is a figure with only one object
    		currentObj = currentFig.getObjects().get(0);
    		
    		attribList = currentObj.getAttributes();
    		
    		Iterator <RavensAttribute> attrItr = attribList.iterator();
    		
    		RavensAttribute attribute;
    		
    		while (attrItr.hasNext())
    		{
    			attribute = attrItr.next();
				attrName = attribute.getName();
				attrValue = attribute.getValue();
				attrValue = attrValue.replaceAll("[-]", "");
				
				if (attrName.equalsIgnoreCase("shape"))
				{
					objectAttributeTable[i][0] = getShapeEncoding(attrValue);
				}
				
				if (attrName.equalsIgnoreCase("size"))
				{
					objectAttributeTable[i][1] = getSizeEncoding(attrValue);
				}
				
				if (attrName.equalsIgnoreCase("fill"))
				{
					objectAttributeTable[i][2] = getFillEncoding(attrValue);
				}
				
				if (attrName.equalsIgnoreCase("angle"))
				{
					objectAttributeTable[i][3] = getAngleEncoding(attrValue);
				}			
    		}	
    	}   	
    }
    
    
    
    public void findTargetChoice(RavensProblem problem, int[] targetValues)
    {
        // String [] fig = {"A","B","C","D","E","F","G","H"};
    	String [] choices = {"1","2","3","4","5","6"};
    	
    	HashMap <String, RavensFigure> figures = problem.getFigures();
    	
    	RavensFigure currentChoice;// = figures.get(fig[0]); 
    	
    	RavensObject currentObj;// = currentFig.getObjects();
    	
    	ArrayList<RavensAttribute> attribList;
    	
    	String attrName, attrValue;
    	int maxMatchChoice = 0, maxMatches = -1;
    	
    	for (int i = 0; i < choices.length; i++)
    	{
    		currentChoice = figures.get(choices[i]);
    		
    		// remember this is a figure with only one object
    		if (currentChoice.getObjects().size() != 1) continue;
    		
    		currentObj = currentChoice.getObjects().get(0);
    		
    		attribList = currentObj.getAttributes();
    		
    		Iterator <RavensAttribute> attrItr = attribList.iterator();
    		
    		RavensAttribute attribute;
    		
    		int valueMatchCount = 0;
    		while (attrItr.hasNext())
    		{
    			attribute = attrItr.next();
				attrName = attribute.getName();
				attrValue = attribute.getValue();
				attrValue = attrValue.replaceAll("[-]", "");
				
				if (attrName.equalsIgnoreCase("shape"))
				{
					if (targetValues[0] == getShapeEncoding(attrValue))
					{
						valueMatchCount++;
					}
				}
				
				if (attrName.equalsIgnoreCase("size"))
				{
					if (targetValues[1] == getSizeEncoding(attrValue))
					{
						valueMatchCount++;
					}
				}
				
				if (attrName.equalsIgnoreCase("fill"))
				{
					if (targetValues[2] == getFillEncoding(attrValue))
					{
						valueMatchCount++;
					}
				}
				
				if (attrName.equalsIgnoreCase("angle"))
				{
					if (targetValues[3] == getAngleEncoding(attrValue))
					{
						valueMatchCount++;
					}
				}			
    		}	
    		
    		 // find choice with maximum targetValues match
    		 if (valueMatchCount > maxMatches)
    		 {
    		 	maxMatchChoice = i;
    		 	maxMatches = valueMatchCount;
    		 } 
    		 
    	}   	
    	probableChoicesStrategy_1[maxMatchChoice] = 1;
    	System.out.println(problem.getName() + "->" + (maxMatchChoice + 1));
    }
     
    /*
    public void findTargetChoice(RavensProblem problem, int[] targetValues)
    {
    	
    }
    */
    public void findAnsChoice_OneObjectStrategy(RavensProblem problem)
    {
    	// First analyze the objectAttributeTable to find out the pattern of change in a row
    	
    	boolean successivePattern = true;
    	int[] attribValueDiff = new int [4]; // 0: shape, 1:size, 2:fill, 3:angle
    	
    	for (int i = 0; i < 4; i++)
    	{
    		if ( (objectAttributeTable[2][i] - objectAttributeTable[1][i]) != 
    	    		 (objectAttributeTable[1][i] - objectAttributeTable[0][i])
    	    	)
    		{
    			successivePattern = false;
    			break;
    		}
    		attribValueDiff[i] = objectAttributeTable[2][i] - objectAttributeTable[1][i];
    	}
    	
    	// a regular magnitude change in size, angle, shape etc.
    	if (successivePattern)
    	{
    		int[] targetValues = new int [4];
    		targetValues[0] = objectAttributeTable[7][0] + attribValueDiff[0];
    		targetValues[1]  = objectAttributeTable[7][1] + attribValueDiff[1];
    		targetValues[2]  = objectAttributeTable[7][2] + attribValueDiff[2];
    		targetValues[3] = (objectAttributeTable[7][3] + attribValueDiff[3]) % 360;
    		
    		// this function writes the correct(?) choice to a shared array.
    		findTargetChoice(problem, targetValues);
    	}
    	
    	else
    	{
    		int[] targetValues = new int [4];
    		
    		targetValues[0] = objectAttributeTable[6][0];
    		targetValues[1] = objectAttributeTable[6][1];
    		targetValues[2] = objectAttributeTable[6][2];
    		targetValues[3] = objectAttributeTable[6][3];
    		
    		// if C's angle is sum of (A +or- B )/360
    		int angleA = objectAttributeTable[0][3];
    		int angleB = objectAttributeTable[1][3];
    		int angleC = objectAttributeTable[2][3];
    		
    		int angleD = objectAttributeTable[3][3];
    		int angleE = objectAttributeTable[4][3];
    		int angleF = objectAttributeTable[5][3];
    		
    		int angleG = objectAttributeTable[6][3];
    		int angleH = objectAttributeTable[7][3];
    		
    		if ((((angleA + angleB)%360) == angleC) && (((angleD + angleE)%360) == angleF))
    		{
    			targetValues[3] = (angleG + angleH)%360; 
    		}
    		
    		if ((((angleA - angleB)%360) == angleC) && (((angleD - angleE)%360) == angleF))
    		{
    			targetValues[3] = (angleG - angleH)%360; 
    		}
    		
    		// this function writes the correct(?) choice to a shared array.
    		findTargetChoice(problem, targetValues);
    	}
    	// System.out.println(successivePattern);
    }
    /* ----------------------------------------------------------   */
    
    public  void Solve3by3(RavensProblem problem)
    {
    	boolean overFigures = true, strategyFound = false;
    	
        int strategy = -1; 
    	
    	// at the end of this step we would have all the required data structures.
    	iterateOverFigures(problem, overFigures, strategy);
    	
    	
    	// System.out.println(oneObjectProblem);
    		
    	overFigures = false;
    	
    	if (suitableFor_3_3_2_objectCountBased())
    	{
    		strategy = 1;
    		strategyFound = true;
    		// System.out.println(problem.getName());
    	}
    	
    	if ((!strategyFound) && suitableFor_3_3_2_figCountBased())
    	{
    		strategy = 2;
    		strategyFound = true;
    		// System.out.println(problem.getName());
    	}
   
    	if ((!strategyFound) && suitableFor_3_3_2_attrCountBased())
    	{
    		strategy = 3;
    		strategyFound = true;
    		// System.out.println(problem.getName());
    	}
    	
    	// this is the matching step for 3-3-2 strategy. Answer might get filled here itself.
    	iterateOverFigures(problem, overFigures, strategy);
    	
    	
    	if ((!strategyFound) && oneObjectProblem) // if strategy is yet not found go for one object based strategy
    	{
    		strategyFound = true;
    		constructAttributeTable(problem);
    		findAnsChoice_OneObjectStrategy(problem);
    	}
    		
    	if ((!strategyFound) && figurePatternProblem)
    	{
    		strategyFound = true;
    		constructfigurePatternTable(problem);
    		System.out.println("------->>>>>>>>>>>>>>>--------");
    		System.out.println(problem.getName());
    		System.out.println("------->>>>>>>>>>>>>>>--------");
    		findAnsChoice_PatternMatchingStrategy(problem);
    		
    	}
    	
    	if (!strategyFound) // if the problem is yet not classified then apply a general not-so-accurate heuristic
    	{
    		strategy = 4;
    		iterateOverFigures(problem, overFigures, strategy);
    	}
    	System.out.println("---------------");
    }

    public  void parse(RavensProblem problem)
    {
    	if (problem.getProblemType().equalsIgnoreCase("3x3"))
    	{
    		Solve3by3(problem);
    	}
    }
    
    /**
     * The primary method for solving incoming Raven's Progressive Matrices.
     * For each problem, your Agent's Solve() method will be called. At the
     * conclusion of Solve(), your Agent should return a String representing its
     * answer to the question: "1", "2", "3", "4", "5", or "6". These Strings
     * are also the Names of the individual RavensFigures, obtained through
     * RavensFigure.getName().
     * 
     * In addition to returning your answer at the end of the method, your Agent
     * may also call problem.checkAnswer(String givenAnswer). The parameter
     * passed to checkAnswer should be your Agent's current guess for the
     * problem; checkAnswer will return the correct answer to the problem. This
     * allows your Agent to check its answer. Note, however, that after your
     * agent has called checkAnswer, it will *not* be able to change its answer.
     * checkAnswer is used to allow your Agent to learn from its incorrect
     * answers; however, your Agent cannot change the answer to a question it
     * has already answered.
     * 
     * If your Agent calls checkAnswer during execution of Solve, the answer it
     * returns will be ignored; otherwise, the answer returned at the end of
     * Solve will be taken as your Agent's answer to this problem.
     * 
     * @param problem the RavensProblem your agent should solve
     * @return your Agent's answer to this problem
     */
    
    public String Solve(RavensProblem problem) {
    	
    	if (!problem.getProblemType().equalsIgnoreCase("3x3"))
    	{
        	attributeDiff = new HashMap<String, String>();
        	
        	diffTableAB = new HashMap<RavensObject, Integer>();
        	
        	diffTableCD = new HashMap<RavensObject, Integer> ();

        	choiceMatchArray  = new int[6];
        	choiceMatchCross  = new int[6];
        	for (int i = 0; i < 6; i++)
        	{
        		choiceMatchArray[i] = 0;
        		choiceMatchCross[i] = 0;
        	}

        	
        	
        	addPosKws();
        	parse_2byx(problem);
        	
        	
        	/*System.out.println("---"+"--");
        	System.out.println(choiceMatchArray[0]+ ","+choiceMatchArray[1]+ ","+choiceMatchArray[2]+","+
        					   choiceMatchArray[3]+","+choiceMatchArray[4]+ ","+choiceMatchArray[5]);
        	
        	System.out.println(choiceMatchCross[0]+ ","+choiceMatchCross[1]+ ","+choiceMatchCross[2]+","+
    				   choiceMatchCross[3]+","+choiceMatchCross[4]+ ","+choiceMatchCross[5]);
        	*/
        	
        	int maxElement = -1, maxElementAlter = -1, countAlter = 0;
        	int maxIndex   = 0, maxIndexAlter = 0, count = 0;
        	for (int i = 0; i < 6; i++)
        	{
        		
        		// System.out.println("-");
        		if(choiceMatchArray[i] > maxElement)
        		{
        			maxIndex = i;
        			maxElement = choiceMatchArray[i];
        			// count++;
        		}
        		// System.out.println(choiceMatchArray[i]);
        		
        		if(choiceMatchCross[i] > maxElementAlter)
        		{
        			maxIndexAlter = i;
        			maxElementAlter = choiceMatchCross[i];
        			// countAlter++;
        		}
        	}

        	int intersect = -1, intersectIndex = -1;
        	int found = 0;
        	
        	
        	// find out confidence level
        	for (int i = 0; i < 6; i++)
        	{
        		found = 0;
        		// System.out.println("-");
        		if(choiceMatchArray[i] == maxElement)
        		{
        			// maxIndex = i;
        			// maxElement = choiceMatchArray[i];
        			found = 1;
        			count++;
        		}
        		// System.out.println(choiceMatchArray[i]);
        		
        		if(choiceMatchCross[i] == maxElementAlter)
        		{
        			// maxIndexAlter = i;
        			// maxElementAlter = choiceMatchCross[i];
        			if (found == 1 && intersectIndex < 0) 
        			{
        					intersect = i; 
        					intersectIndex = i; 
        			}
        			countAlter++;
        		}
        	}
        	if( problem.getProblemType().endsWith("2")) //  2x2
        	{
        		// System.out.println("------------------2x2----------");
    	    	if (count > 1 && countAlter == 1 && (maxElementAlter - maxElement) > 0)
    	    	{
    	    		maxIndex = maxIndexAlter;
    	    	}
    	    	
    	    	else if (intersect > 0 && maxElement >= 0)
    	    	{
    	    		maxIndex = intersectIndex;
    	    	}
        	}
        	// System.out.println("------------------");
        	
        	// System.out.println(maxElement);
        	
        	System.out.println("Answered");
        	
            return (new Integer(maxIndex + 1)).toString();

    	}
    	
    	// else solve for 3x3
    	
    	// this table keeps track of counts of each unique object
    	// if a square with same attribs occurs twice in then count would be 2 
    	objectCountMap = new HashMap <String, Integer> ();
    	
    	// this table keeps track of counts of each unique figure
    	// if a figure with same attribs and objects occurs twice in then count would be 2
    	figureCountMap = new HashMap <String, Integer> ();
    	

    	// this table keeps track of counts of each unique attribute - Not so reliable strategy
    	// we will use this only when above two strategies fail
    	// if an attribute with same values occurs twice in then count would be 2
    	attributeCountMap = new HashMap <String, Integer> ();

    	
    	// this table is for only such class of problems which have only one object in each figure
    	objectAttributeTable = new int [8][4];
    	
    	
    	// this table is for only such class of problems which have only one object in each figure
    	figureAttributeTable = new int [8][5];
    	
    	// let the default values be -1
    	for (int i = 0; i < 8; i++)
    	{
    		for (int j = 0; j < 5; j++)
    		{
    			figureAttributeTable[i][j] = -1;
    		}
    	}

    	
    	probableChoicesStrategy_1 = new int [6];
    	for (int i = 0; i < 6; i++)
    	{
    		probableChoicesStrategy_1[i] = -1;
    	}
    	
    	parse(problem);
    	
    	
    	for (int i = 0; i < 8; i++)
    	{
    		for (int j = 0; j < 0; j++)
    		{
    			System.out.print(figureAttributeTable[i][j] + ",");
    		}
    		// System.out.println("");
    	}
        

    	String answer = "";
    	boolean answered = false;
    	System.out.println("------ANSWER----------");
    	for (int i = 0; i < 6; i++)
    	{
    		if (probableChoicesStrategy_1[i] == 1)
    		{
    			answered = true;
    			answer = (new Integer(i+1)).toString();
    			System.out.println(problem.getName() + "->"+(i+1));
    		}
    	}
    	
    	// make sure no question is left un-answered
    	
    	if (!answered)
    	{
    		answer = (new Integer(4)).toString();
    	}
    	System.out.println("------ANSWER----------");
    	
    	Collection <Integer> figCounts = figureCountMap.values();
    	
    	objectCountMap.clear();
    	figureCountMap.clear();
    	attributeCountMap.clear();
    	
        return answer;
    }
}
