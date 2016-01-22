
package com.its.core.util;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Provides the the ability to use simple XML property files. Each property is
 * in the form X.Y.Z, which would map to an XML snippet of:
 * <pre>
 * &lt;X&gt;
 *     &lt;Y&gt;
 *         &lt;Z&gt;someValue&lt;/Z&gt;
 *     &lt;/Y&gt;
 * &lt;/X&gt;
 * </pre>
 *
 * The XML file is passed in to the constructor and must be readable and
 * writtable. Setting property values will automatically persist those value
 * to disk.
 */
public class XMLProperties extends Properties{

	private static final long serialVersionUID = -1362916023934787939L;
	
	private static final Log log = LogFactory.getLog(XMLProperties.class);
	
	private File xmlFileDir;
	//private Document doc;

	/**
	 * Parsing the XML file every time we need a property is slow. Therefore,
	 * we use a Map to cache property values that are accessed more than once.
	 */
	private Map<String, String> propertyCache = new HashMap<String, String>();

	/**
	 * Creates a new XMLProperties object.
	 *
	 * @parm file the full path the file that properties should be read from
	 *      and written to.
	 */
	public XMLProperties(String file){
		this.xmlFileDir = new File(file);
		/*
		try {
			SAXBuilder builder = new SAXBuilder();
			// Strip formatting
			DataUnformatFilter format = new DataUnformatFilter();
			builder.setXMLFilter(format);
			doc = builder.build(new File(file));
		}
		catch (Exception ex) {
			log.fatal("初始化参数配置文件'"+file+"'出错："+ex.getMessage(),ex);
			ex.printStackTrace();
		}
		*/
	}
	
	private Document getDocument(File file){
		Document doc = null;
		try {
			SAXBuilder builder = new SAXBuilder();
			DataUnformatFilter format = new DataUnformatFilter();
			builder.setXMLFilter(format);
			doc = builder.build(file);
		}
		catch (Exception ex) {
			log.fatal("初始化参数配置文件'"+file+"'出错："+ex.getMessage(),ex);
			ex.printStackTrace();
		}
		return doc;		
	}
	
	private File[] getConfigFiles(){
		return this.xmlFileDir.listFiles(new FilenameFilterByPostfix("param-config",".xml",true));	
	}

	/**
	 * Returns the value of the specified property.
	 * @param name the name of the property to get.
	 * @return the value of the specified property.
	 */
	public String getProperty(String name) {
		if (propertyCache.containsKey(name)) {
			return (String) propertyCache.get(name);
		}

		String[] propName = parsePropertyName(name);
		// Search for this property by traversing down the XML heirarchy.
		
		File[] configFiles = this.getConfigFiles();
		//System.out.println(configFiles.length);
		int size = configFiles.length;
		int len = propName.length;
		for(int j=0;j<size;j++){
			Document doc = this.getDocument(configFiles[j]);
			Element element = doc.getRootElement();			
			boolean found = true;
			for (int i = 0; i < len; i++) {
				String attributeValue = element.getAttributeValue(propName[i]);
				element = element.getChild(propName[i]);
				
				if (element == null) {
					// This node doesn't match this part of the property name which
					// indicates this property doesn't exist so return null.				
					if(attributeValue==null || "".equals(attributeValue) || i!=(len-1)){
						found = false;
						break;
					}
					else {
						propertyCache.put(name, attributeValue);
						return attributeValue;					
					}
				}
			}
			// At this point, we found a matching property, so return its value.
			// Empty strings are returned as null.
			
			if(!found) continue;
			
			String value = element.getText();
			if ("".equals(value)) {
				return null;
			}
			else {
				// Add to cache so that getting property next time is fast.
				value = value.trim();
				propertyCache.put(name, value);
				return value;
			}
		}
		return null;
	}
	
	private String getProperty(Element element,String name,String key) {
		String[] propName = parsePropertyName(name);
		int len = propName.length;
		for (int i = 0; i < len; i++) {
			String attributeValue = element.getAttributeValue(propName[i]);
			element = element.getChild(propName[i]);
			if (element == null) {				
				if(attributeValue==null || "".equals(attributeValue) || i!=(len-1)){
					return null;
				}
				else {
					propertyCache.put(key, attributeValue);
					return attributeValue;					
				}				
			}
		}
		// At this point, we found a matching property, so return its value.
		// Empty strings are returned as null.
		String value = element.getText();
		if ("".equals(value)) {
			return null;
		}
		else {
			// Add to cache so that getting property next time is fast.
			value = value.trim();
			//System.out.println(key+":"+value);
			propertyCache.put(key, value);
			return value;
		}
	}	
	
	public String getProperty(String prefix,int no,String postfix) {		
		String key = prefix+"."+no+"."+postfix;
		if (propertyCache.containsKey(key)) {
			return (String) propertyCache.get(key);
		}		
		Element ele = this.getElement(prefix,no);
		if(ele!=null){
			return this.getProperty(ele,postfix,key);
		}
		return null;
	}
	
	public String getProperty(String prefix1,int no1,String prefix2,int no2,String postfix) {	
		String key = prefix1+"."+no1+"."+prefix2+"."+no2+"."+postfix;
		if (propertyCache.containsKey(key)) {
			return (String) propertyCache.get(key);
		}	
					
		Element ele = this.getElement(prefix1,no1);
		
		return this.getProperty(ele, key,prefix2,no2,postfix);		
	}	
	
	public String getProperty(String prefix1,int no1,String prefix2,int no2,String prefix3,int no3,String postfix) {	
		String key = prefix1+"."+no1+"."+prefix2+"."+no2+"."+prefix3+"."+no3+"."+postfix;
		if (propertyCache.containsKey(key)) {
			return (String) propertyCache.get(key);
		}	
					
		Element ele = this.getElement(prefix1,no1);
		ele = this.getElement(ele, prefix2, no2);
		return this.getProperty(ele, key,prefix3,no3,postfix);
	}	
	
	public String getProperty(String[] prefixArr,int[] noArr,String postfix) {
		StringBuilder keyBuilder = new StringBuilder();
		int len = prefixArr.length;
		for(int i=0;i<len;i++){
			keyBuilder.append(prefixArr[i]).append(".").append(noArr[i]).append(".");
		}
		keyBuilder.append(postfix);
		String key = keyBuilder.toString();
		if (propertyCache.containsKey(key)) {
			return (String) propertyCache.get(key);
		}			
		
		Element ele = this.getElement(prefixArr[0],noArr[0]);
		for(int i=1;i<len-1;i++){
			ele = this.getElement(ele, prefixArr[i],noArr[i]);
		}
		return this.getProperty(ele, key,prefixArr[len-1],noArr[len-1],postfix);
	}
	
	private String getProperty(Element ele,String key,String prefix,int no,String postfix) {
		if(ele!=null){			
			String[] propName = parsePropertyName(prefix);
			int len = propName.length;
			List eleList = null;
			Element element = ele.getChild(propName[0]);
			for (int i = 1; i < len; i++) {
				//System.out.println(element);
				if(i==len-1){
					if(element.getChild(propName[i])!=null) eleList = element.getChildren();
				}
				else{			
					element = element.getChild(propName[i]);			
					if (element == null) {
						break;		
					}
				}
			}		
			
			if(eleList!=null){
				int eleSize = eleList.size();
				if(no<=eleSize-1){					
					Element tmpEle = (Element)eleList.get(no);
					//System.out.println(tmpEle);
					return this.getProperty(tmpEle,postfix,key);		
				}
			}							
		}
		return null;		
	}
	
	private Element getElement(String prefix,int no) {
		//String key = prefix+"."+no+"."+postfix;

		String[] propName = parsePropertyName(prefix);
		// Search for this property by traversing down the XML heirarchy.

		File[] configFiles = this.getConfigFiles();
		int size = configFiles.length;
		int len = propName.length;
		List eleList = null;
		for(int j=0;j<size;j++){
			Document doc = this.getDocument(configFiles[j]);
			Element element = doc.getRootElement();			
			for (int i = 0; i < len; i++) {
				//System.out.println(element);
				if(i==len-1){
					if(element.getChild(propName[i])!=null) eleList = element.getChildren();
				}
				else{			
					element = element.getChild(propName[i]);			
					if (element == null) {
						break;		
					}
				}
			}
			
			if(eleList!=null) break;
		}
		
		if(eleList!=null){
			int eleSize = eleList.size();
			if(no>eleSize-1){
				return null;
			}
			Element tmpEle = (Element)eleList.get(no);
			return tmpEle;		
		}
				
		return null;
	}	
	
	private Element getElement(Element element,String prefix,int no) {
		String[] propName = parsePropertyName(prefix);
		int len = propName.length;
		List eleList = null;		
		for (int i = 0; i < len; i++) {
			//System.out.println(element);
			if(i==len-1){
				if(element.getChild(propName[i])!=null) eleList = element.getChildren();
			}
			else{			
				element = element.getChild(propName[i]);			
				if (element == null) {
					break;		
				}
			}
		}		
		
		if(eleList!=null){
			int eleSize = eleList.size();
			if(no>eleSize-1){
				return null;
			}
			Element tmpEle = (Element)eleList.get(no);
			return tmpEle;		
		}
				
		return null;		
	}
	
	public int getPropertyNum(String name){
		int num = 0;
		String[] propName = parsePropertyName(name);
		
		File[] configFiles = this.getConfigFiles();
		int size = configFiles.length;
		int len = propName.length;
		List eleList = null;		
		for(int j=0;j<size;j++){
			Document doc = this.getDocument(configFiles[j]);
			Element element = doc.getRootElement();
			for (int i = 0; i < len; i++) {
				if(i==len-1){
					eleList = element.getChildren();
				}
				else{			
					element = element.getChild(propName[i]);			
					if (element == null) {						
						break;	
					}
				}
			}
			if(eleList!=null) break;
		}
		if(eleList!=null){
			num = eleList.size();
		}	
		return num;
	}
	
	private int getPropertyNum(Element element,String name){
		int num = 0;
		String[] propName = parsePropertyName(name);
		int len = propName.length;
		for (int i = 0; i < len; i++) {
			if(i==len-1){
				num = element.getChildren().size();
			}
			else{			
				element = element.getChild(propName[i]);			
				if (element == null) {						
					break;	
				}
			}
		}
		return num;		
	}
	
	public int getPropertyNum(String prefix,int no,String postfix){
		Element ele = this.getElement(prefix,no);
		return this.getPropertyNum(ele, postfix);
		/*
		//System.out.println(ele);
		if(ele!=null){
			//num = this.getPropertyNum(ele,postfix);
			List eleList = ele.getChildren();
			//System.out.println("eleList size = "+eleList.size());
			if(eleList!=null){
				int eleSize = eleList.size();
				
				for(int i=0;i<eleSize;i++){
					Element tmpEle = (Element)eleList.get(i);
					//System.out.println("tmpEle.getName() = "+tmpEle.getName());
					if(tmpEle.getName().equals(postfix)){
						num = this.getPropertyNum(tmpEle,postfix);	
						break;
					}
				}
			}						
		}
		return num;
		*/
	}	
	
	/**
	 * 
	 * @param prefix1
	 * @param no1
	 * @param prefix2
	 * @param no2
	 * @param postfix
	 * @return
	 * 例子：int num = xmlProperties.getPropertyNum("tiip.realtime_vehicle.filters.filter",2,"sections.section",1,"check_points");
	 */
	public int getPropertyNum(String prefix1,int no1,String prefix2,int no2,String postfix){
		Element ele = this.getElement(prefix1,no1);
		ele = this.getElement(ele, prefix2,no2);
		return this.getPropertyNum(ele, postfix);
		//System.out.println(ele);
		
		/*
		if(ele!=null){
			List eleList = ele.getChildren();
			if(eleList!=null){
				int eleSize = eleList.size();
				
				for(int i=0;i<eleSize;i++){
					Element tmpEle = (Element)eleList.get(i);
					//System.out.println("tmpEle.getName() = "+tmpEle.getName());
					if(tmpEle.getName().equals(postfix)){
						num = this.getPropertyNum(tmpEle,postfix);	
						break;
					}
				}
			}						
		}
		return num;
		*/
	}		
	
	public int getPropertyNum(String[] prefixArr,int[] noArr,String postfix){
		Element ele = this.getElement(prefixArr[0],noArr[0]);
		int len = prefixArr.length;
		for(int i=1;i<len;i++){
			ele = this.getElement(ele, prefixArr[i],noArr[i]);
		}
		return this.getPropertyNum(ele, postfix);
	}	

	/**
	 * Return all children property names of a parent property as a String array,
	 * or an empty array if the if there are no children. For example, given
	 * the properties <tt>X.Y.A</tt>, <tt>X.Y.B</tt>, and <tt>X.Y.C</tt>, then
	 * the child properties of <tt>X.Y</tt> are <tt>A</tt>, <tt>B</tt>, and
	 * <tt>C</tt>.
	 *
	 * @param parent the name of the parent property.
	 * @return all child property values for the given parent.
	 */
	public String[] getChildrenProperties(Document doc,String parent) {
		String[] propName = parsePropertyName(parent);
		// Search for this property by traversing down the XML heirarchy.
		Element element = doc.getRootElement();
		for (int i = 0; i < propName.length; i++) {
			element = element.getChild(propName[i]);
			if (element == null) {
				// This node doesn't match this part of the property name which
				// indicates this property doesn't exist so return empty array.
				return new String[] {};
			}
		}
		// We found matching property, return names of children.
		List children = element.getChildren();
		int childCount = children.size();
		String[] childrenNames = new String[childCount];
		for (int i = 0; i < childCount; i++) {
			childrenNames[i] = ( (Element) children.get(i)).getName();
		}
		return childrenNames;
	}

	/**
	 * Sets the value of the specified property. If the property doesn't
	 * currently exist, it will be automatically created.
	 * @param name the name of the property to set.
	 * @param value the new value for the property.
	 */
	/*
	public Object setProperty(String name, String value) {
		// Set cache correctly with prop name and value.
		propertyCache.put(name, value);

		String[] propName = parsePropertyName(name);
		// Search for this property by traversing down the XML heirarchy.
		Element element = doc.getRootElement();
		for (int i = 0; i < propName.length; i++) {
			// If we don't find this part of the property in the XML heirarchy
			// we add it as a new node
			if (element.getChild(propName[i]) == null) {
				element.addContent(new Element(propName[i]));
			}
			element = element.getChild(propName[i]);
		}
		// Set the value of the property in this node.
		element.setText(value);
		// write the XML properties to disk
		saveProperties();
		return null;
	}
	*/
	
	/**
	 * Deletes the specified property.
	 * @param name the property to delete.
	 */
	/*
	public void deleteProperty(String name) {
		String[] propName = parsePropertyName(name);
		// Search for this property by traversing down the XML heirarchy.
		Element element = doc.getRootElement();
		for (int i = 0; i < propName.length - 1; i++) {
			element = element.getChild(propName[i]);
			// Can't find the property so return.
			if (element == null) {
				return;
			}
		}
		// Found the correct element to remove, so remove it...
		element.removeChild(propName[propName.length - 1]);
		// .. then write to disk.
		saveProperties();
	}
	*/

	/**
	 * Saves the properties to disk as an XML document. A temporary file is
	 * used during the writing process for maximum safety.
	 */
	/*
	private synchronized void saveProperties() {
		OutputStream out = null;
		boolean error = false;
		// Write data out to a temporary file first.
		File tempFile = null;
		try {
			tempFile = new File(file.getParentFile(), file.getName() + ".tmp");
			// Use JDOM's XMLOutputter to do the writing and formatting. The
			// file should always come out pretty-printed.
			XMLOutputter outputter = new XMLOutputter("    ", true);
			out = new BufferedOutputStream(new FileOutputStream(tempFile));
			outputter.output(doc, out);
		}
		catch (Exception e) {
			e.printStackTrace();
			// There were errors so abort replacing the old property file.
			error = true;
		}
		finally {
			try {
				out.close();
			}
			catch (Exception e) {
				e.printStackTrace();
				error = true;
			}
		}
		// No errors occured, so we should be safe in replacing the old
		if (!error) {
			// Delete the old file so we can replace it.
			file.delete();
			// Rename the temp file. The delete and rename won't be an
			// automic operation, but we should be pretty safe in general.
			// At the very least, the temp file should remain in some form.
			tempFile.renameTo(file);
		}
	}
	*/
	
	/**
	 * Returns an array representation of the given Jive property. Jive
	 * properties are always in the format "prop.name.is.this" which would be
	 * represented as an array of four Strings.
	 * @param name the name of the Jive property.
	 * @return an array representation of the given Jive property.
	 */
	private String[] parsePropertyName(String name) {
		// Figure out the number of parts of the name (this becomes the size
		// of the resulting array).
		int size = 1;
		for (int i = 0; i < name.length(); i++) {
			if (name.charAt(i) == '.') {
				size++;
			}
		}
		String[] propName = new String[size];
		// Use a StringTokenizer to tokenize the property name.
		StringTokenizer tokenizer = new StringTokenizer(name, ".");
		int i = 0;
		while (tokenizer.hasMoreTokens()) {
			propName[i] = tokenizer.nextToken();
			i++;
		}
		return propName;
	}
	
	/**
	 * 只返回访问过的属性名集合
	 */
	@SuppressWarnings("unchecked")
	public Set keySet() {
		Set keySet = null;
		if (this.propertyCache != null)
			keySet = this.propertyCache.keySet();
		else		
			keySet = new HashSet();
		return keySet;
	}	


	public void clearProperties() {
		this.propertyCache.clear();
		//this.propertyCache.keySet().clear();
	}

	public static void main(String[] args) {
		String pathName = "D:/nio/mina/project/mina-1.1.2/tiip/config/";
		System.out.println("++++++++++++++ " + File.separator);
		System.out.println("PathName = " + pathName);
		try {
			XMLProperties xmlProperties = new XMLProperties(pathName);
			
			
			int num1 = xmlProperties.getPropertyNum("tiip.realtime_vehicle.filters.filter");
			System.out.println("num1 = "+num1);
			
			
			int num2 = xmlProperties.getPropertyNum("tiip.realtime_vehicle.filters.filter",2,"sections.section");
			System.out.println("num2 = "+num2);
			
			String t0 = xmlProperties.getProperty("tiip.realtime_vehicle.filters.filter",2,"class");
			System.out.println(t0);			
			
			System.out.println("=========================");
			String t1 = xmlProperties.getProperty("tiip.realtime_vehicle.filters.filter",2,"sections.section",1,"name");
			System.out.println("t1="+t1);
			
			
			int num3 = xmlProperties.getPropertyNum("tiip.realtime_vehicle.filters.filter",2,"sections.section",1,"check_points.check_point");
			System.out.println("num3 = "+num3);
			
			String t3 = xmlProperties.getProperty("tiip.realtime_vehicle.filters.filter",2,"sections.section",1,"check_points.check_point",2,"device_ids");
			System.out.println("t3 = "+t3);
			
			
			int num4 = xmlProperties.getPropertyNum(new String[]{"tiip.realtime_vehicle.filters.filter","sections.section"},new int[]{2,1},"check_points.check_point");
			System.out.println("num4 = "+num4);
			
			String t4 = xmlProperties.getProperty(new String[]{"tiip.realtime_vehicle.filters.filter","sections.section","check_points.check_point"},new int[]{2,1,2},"device_ids");
			System.out.println("t4 = "+t4);
			
			/*
			String rul = xmlProperties.getProperty("tiip.version.no");
			System.out.println("new value 1 = " + rul);
			
			rul = xmlProperties.getProperty("tiip.timers.task",1,"imp_class");
			System.out.println("new value 2 = " + rul);	
			
			//System.out.println("property num = "+xmlProperties.getPropertyNum(null,"tiip.mytest.test"));		
			*/
//			String[] arr = xmlProperties.getChildrenProperties("tiip.mytest.test");
//			int len = arr.length;
//			for(int i=0;i<len;i++){
//				System.out.println(i+":"+arr[i]);
//			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
