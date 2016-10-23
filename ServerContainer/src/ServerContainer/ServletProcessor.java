package ServerContainer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ServletProcessor {

	public static Map<String, String> urlMapping = parseWebXML();
	public static List<String> jspFiles = getJSPList();

	public static void processServletRequest(MyServletRequest req, MyServletResponse res) throws Exception {
		String uri = req.getURI();

		
		String servletName = null;
		if(jspFiles.contains(uri.substring(1))){
			servletName = "Servlet."+uri.substring(1, uri.length()).replace('.', '_') ;
			JspParser.Jsp2Servlet(uri.substring(1));
			urlMapping.put(uri, servletName);
		}else{
			servletName = getServerName(uri);
		}
	   // servletName = getServerName(uri);
		if (servletName == null) {
			System.out.println("Servlet: " + servletName + " is not found!!!");
			throw new NullPointerException("404");
		}
		System.out.println("Processing servlet: " + servletName);
		// 加载servlet类
		Servlet servlet = loadServlet(servletName);
		// 将request和response交给Servlet处理
		callService(servlet, req, res);
	}

	private static Servlet loadServlet(String servletName) throws MalformedURLException {
		// String servletURL = "../" + servletName.replace('.', '/');
		String servletURL = System.getProperty("user.dir") + File.separator + "bin";
		File file = new File(servletURL);
		// URL url = new URL("file://Servlet/LoginServlet");
		URL url = file.toURL();
		URLClassLoader loader = new URLClassLoader(new URL[] { url }, Thread.currentThread().getContextClassLoader());
		Servlet servlet = null;

		try {
			@SuppressWarnings("unchecked")
			Class<Servlet> servletClass = (Class<Servlet>) loader.loadClass(servletName);
			servlet = (Servlet) servletClass.newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return servlet;
	}

	private static void callService(Servlet servlet, ServletRequest request, ServletResponse response) {
		try {
			servlet.service(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private static String getServerName(String uri) throws Exception {
		return urlMapping.get(uri);
	}

	static Map<String, String> parseWebXML() {
		Map<String, String> urls = new HashMap<>();
		try {
			urls = new HashMap<>();
			Map<String, String> servletMapping = null;
			Map<String, String> servlet = null;
			servlet = new HashMap<>();
			servletMapping = new HashMap<>();
			// parse web.xml
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new File("web.xml"));
			// get root element ---<web-app></web-app>
			Element node = doc.getRootElement();

			Iterator<Element> servletMappings = node.elementIterator("servlet-mapping");
			while (servletMappings.hasNext()) {
				Element e = servletMappings.next();
				Iterator<Element> urlPatterns = e.elementIterator("url-pattern");
				Iterator<Element> servletNames = e.elementIterator("servlet-name");
				if (urlPatterns.hasNext() && servletNames.hasNext()) {
					servletMapping.put(urlPatterns.next().getText(), servletNames.next().getText());
				}
			}

			Iterator<Element> servlets = node.elementIterator("servlet");
			while (servlets.hasNext()) {
				Element e = servlets.next();
				Iterator<Element> servletNames = e.elementIterator("servlet-name");
				Iterator<Element> servletClasses = e.elementIterator("servlet-class");
				if (servletNames.hasNext() && servletClasses.hasNext()) {
					servlet.put(servletNames.next().getText(), servletClasses.next().getText());
				}
			}

			for (String url : servletMapping.keySet()) {
				urls.put(url, servlet.get(servletMapping.get(url)));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return urls;

	}

	static void parseJSP(File f) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(f));
		File javaFile = new File(System.getProperty("user.dir") + File.separator + "bin" + File.separator + "jsp");
		javaFile.mkdir();
		BufferedWriter bw = new BufferedWriter(new FileWriter(javaFile));
		boolean mode = false;
		String content = br.readLine();
		while (content != null) {
			System.err.println(content);
			if (content.indexOf("<%") != -1) {

			}
			content = br.readLine();
		}
		br.close();
		bw.close();
	}
	
	static List<String> getJSPList(){
		ArrayList<String> jspFiles = new ArrayList<>();
		File webroot = new File(System.getProperty("user.dir")+File.separator+"src"+File.separator+"JSP");
		File[] webContent = webroot.listFiles();

		for (File f : webContent) {
			if (f.getName().endsWith(".jsp")) {
				jspFiles.add(f.getName());
			}
		}
		return jspFiles;
	}
}
