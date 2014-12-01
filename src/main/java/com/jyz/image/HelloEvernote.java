package com.eighth.airrent.test;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.clients.UserStoreClient;
import com.evernote.edam.error.EDAMErrorCode;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.transport.TTransportException;

public class HelloEvernote {
	 // 去这里申请一个 Token https://sandbox.evernote.com/api/DeveloperToken.action
	  private static final String AUTH_TOKEN = "S=s1:U=8fee5:E=1513f302daa:C=149e77efea0:P=1cd:A=en-devtoken:V=2:H=c51eaf9567047bc98b3eccc0ded28070";
	 
	  private UserStoreClient userStore;
	  private NoteStoreClient noteStore;
	 
	  public static void main(String args[]) throws Exception {
	    String token = System.getenv("AUTH_TOKEN");
	    if (token == null) {
	      token = AUTH_TOKEN;
	    }
	    if ("".equals(token)) {
	      System.err.println("去这里申请一个Token https://sandbox.evernote.com/api/DeveloperToken.action");
	      return;
	    }
	 
	    try {
	    	HelloEvernote demo = new HelloEvernote(token);
	      demo.listNotes();
	    //  demo.createNote();
//	      demo.searchNotes();
//	      demo.updateNoteTag();
	    } catch (EDAMUserException e) {
	      // 需要处理的异常
	      if (e.getErrorCode() == EDAMErrorCode.AUTH_EXPIRED) {
	        System.err.println("授权的Token已经过期！");
	      } else if (e.getErrorCode() == EDAMErrorCode.INVALID_AUTH) {
	        System.err.println("无效的授权Token！");
	      } else if (e.getErrorCode() == EDAMErrorCode.QUOTA_REACHED) {
	        System.err.println("无效的授权Token！");
	      } else {
	        System.err.println("错误： " + e.getErrorCode().toString()
	            + " 参数： " + e.getParameter());
	      }
	    } catch (EDAMSystemException e) {
	      System.err.println("系统错误： " + e.getErrorCode().toString());
	    } catch (TTransportException t) {
	      System.err.println("网络错误：" + t.getMessage());
	    }
	  }
	 
	  /**
	   * 初始化 UserStore and NoteStore 客户端
	   */
	  public HelloEvernote(String token) throws Exception {
	    // 设置 UserStore 的客户端并且检查和服务器的连接
	    EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX, token);
	    ClientFactory factory = new ClientFactory(evernoteAuth);
	    userStore = factory.createUserStoreClient();
	 
	    boolean versionOk = userStore.checkVersion("Evernote EDAMDemo (Java)",
	        com.evernote.edam.userstore.Constants.EDAM_VERSION_MAJOR,
	        com.evernote.edam.userstore.Constants.EDAM_VERSION_MINOR);
	    if (!versionOk) {
	      System.err.println("不兼容的Evernote客户端协议");
	      System.exit(1);
	    }
	 
	    // 设置 NoteStore 客户端
	    noteStore = factory.createNoteStoreClient();
	  }
	 
	  /**
	   * 获取并显示用户的笔记列表
	   */
	  private void listNotes() throws Exception {
	    // 列出用户的Notes
	    // 首先，获取一个笔记本的列表
	    List<Notebook> notebooks = noteStore.listNotebooks();
	    for (Notebook notebook : notebooks) {
	      // 然后，搜索笔记本中前100个笔记并按创建日期排序
	      NoteFilter filter = new NoteFilter();
	      filter.setNotebookGuid(notebook.getGuid());
	      filter.setOrder(NoteSortOrder.CREATED.getValue());
	      filter.setAscending(true);
	      NoteList noteList = noteStore.findNotes(filter, 0, 10000);
	      List<Note> notes = noteList.getNotes();
	      for (Note note : notes) {
	        Note fullNote = noteStore.getNote(note.getGuid(), true, true, false,
	  	          false);
	  	     getDataByString(fullNote.getContent());
	      }
	    }
	  
	  }
//	 
//	  /**
//	   * 创建一个新的笔记
//	   */
//	  private void createNote() throws Exception {
//	    // 创建一个新的笔记对象，并填写相关内容，比如标题等
//	    Note note = new Note();
//	    note.setTitle("Yotoo的Demo演示：通过Java创建一个新的笔记");
//	 
//	    String fileName = "enlogo.png";
//	    String mimeType = "image/png";
//	 
//	    // 给笔记添加一个附件，比如图片；首先创建一个资源对象，然后设置相关属性，比如文件名
//	    Resource resource = new Resource();
//	    resource.setData(readFileAsData(fileName));
//	    resource.setMime(mimeType);
//	    ResourceAttributes attributes = new ResourceAttributes();
//	    attributes.setFileName(fileName);
//	    resource.setAttributes(attributes);
//	 
//	    //现在，给新的笔记增加一个新的资源对象
//	    note.addToResources(resource);
//	 
//	    // 这个资源对象作为笔记的一部分显示
//	    String hashHex = bytesToHex(resource.getData().getBodyHash());
//	 
//	    // Evernote笔记的内容是通过ENML（Evernote Markup Language）语言生成的。
//	    // 在这里可以了解具体的说明 http://dev.evernote.com/documentation/cloud/chapters/ENML.php
//	    String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
//	        + "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">"
//	        + "<en-note>"
//	        + "<span style=\"color:green;\">Evernote的图标在这里，很绿奥！</span><br/>"
//	        + "<en-media type=\"image/png\" hash=\"" + hashHex + "\"/>"
//	        + "</en-note>";
//	    note.setContent(content);
//	 
//	    // 最后，使用createNote方法，发送一个新的笔记给Evernote。
//	    // 返回的新笔记对象将包含服务器生成的属性,如新笔记的的GUID
//	    Note createdNote = noteStore.createNote(note);
//	    newNoteGuid = createdNote.getGuid();
//	 
//	    System.out.println("成功创建一个新的笔记，GUID为：" + newNoteGuid);
//	    System.out.println();
//	  }
//	 
//	  /**
//	   * 查询用户的笔记并显示结果
//	   */
//	  private void searchNotes() throws Exception {
//	    // 搜索的格式需要根据Evernote搜索语法，
//	    // 参考这里http://dev.evernote.com/documentation/cloud/chapters/Searching_notes.php
//	 
//	    // 在这个例子中，我们搜索的标题中包含Yotoo
//	    String query = "intitle:Yotoo";
//	 
//	    // 搜索的笔记中包含具体标签，我可以使用这个：
//	    // String query = "tag:tagname";
//	    // 搜索任何位置包含"Yotoo"的笔记，可以使用：
//	    // String query = "Yotoo";
//	 
//	    NoteFilter filter = new NoteFilter();
//	    filter.setWords(query);
//	    filter.setOrder(NoteSortOrder.UPDATED.getValue());
//	    filter.setAscending(false);
//	 
//	    // 查询前50个满足条件的笔记
//	    System.out.println("满足查询条件的笔记： " + query);
//	    NoteList notes = noteStore.findNotes(filter, 0, 50);
//	    System.out.println("找到 " + notes.getTotalNotes() + " 个笔记");
//	 
//	    Iterator<Note> iter = notes.getNotesIterator();
//	    while (iter.hasNext()) {
//	      Note note = iter.next();
//	      System.out.println("笔记： " + note.getTitle());
//	 
//	      // 通过findNotes()返回的Note对象，仅包含笔记的属性，标题，GUID，创建时间等，但笔记的内容和二进制数据都省略了；
//	      // 获取笔记的内容和二进制资源,可以调用getNote()方法获取
//	      Note fullNote = noteStore.getNote(note.getGuid(), true, true, false,
//	          false);
//	      System.out.println("笔记包含 " + fullNote.getResourcesSize()
//	          + " 个资源对象");
//	      System.out.println();
//	    }
//	  }
//	 
//	  /**
//	   * 更新标签分配给一个笔记。这个方法演示了如何调用updateNote方法发送被修改字段
//	   */
//	  private void updateNoteTag() throws Exception {
//	    // 当更新一个笔记时，只需要发送已经修改的字段。
//	    // 例如，通过updateNote发送的Note对象中没有包含资源字段，那么Evernote服务器不会修改已经存在的资源属性
//	    // 在示例代码中,我们获取我们先前创建的笔记,包括 笔记的内容和所有资源。
//	    Note note = noteStore.getNote(newNoteGuid, true, true, false, false);
//	 
//	    //现在,更新笔记。复原内容和资源，因为没有修改他们。我们要修改的是标签。
//	    note.unsetContent();
//	    note.unsetResources();
//	 
//	    // 设置一个标签
//	    note.addToTagNames("TestTag");
//	 
//	    // 现在更新笔记，我们没有设置内容和资源，所以他们不会改变
//	    noteStore.updateNote(note);
//	    System.out.println("成功增加标签");
//	 
//	    // 证明一下我们没有修改笔记的内容和资源；重新取出笔记，它仍然只有一个资源（图片）
//	    note = noteStore.getNote(newNoteGuid, false, false, false, false);
//	    System.out.println("更新以后, 笔记有 " + note.getResourcesSize()
//	        + " 个资源");
//	    System.out.println("更新以后，笔记的标签是： ");
//	    for (String tagGuid : note.getTagGuids()) {
//	      Tag tag = noteStore.getTag(tagGuid);
//	      System.out.println("* " + tag.getName());
//	    }
//	 
//	    System.out.println();
//	  }
//	    /**
//	     * 从磁盘读取文件的内容并创建数据对象
//	     */
//	    private static Data readFileAsData(String fileName) throws Exception {
//	        String filePath = new File(HelloEvernote.class.getResource(
//	                "com.yotoo.app.HelloEvernote.class").getPath()).getParent()
//	                + File.separator + fileName;
//	        // 读取文件的二进制内容
//	        FileInputStream in = new FileInputStream(filePath);
//	        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//	        byte[] block = new byte[10240];
//	        int len;
//	        while ((len = in.read(block)) >= 0) {
//	            byteOut.write(block, 0, len);
//	        }
//	        in.close();
//	        byte[] body = byteOut.toByteArray();
//	 
//	        // 创建一个新的包含文件内容的二进制对象
//	        Data data = new Data();
//	        data.setSize(body.length);
//	        data.setBodyHash(MessageDigest.getInstance("MD5").digest(body));
//	        data.setBody(body);
//	 
//	        return data;
//	    }
//	 
//	  /**
//	   * 把byte数组转换成hexadecimal字符串
//	   */
//	  public static String bytesToHex(byte[] bytes) {
//	    StringBuilder sb = new StringBuilder();
//	    for (byte hashByte : bytes) {
//	      int intVal = 0xff & hashByte;
//	      if (intVal < 0x10) {
//	        sb.append('0');
//	      }
//	      sb.append(Integer.toHexString(intVal));
//	    }
//	    return sb.toString();
//	  }

	private void getDataByString(String content) {
	//	System.out.println(content);
		String[] str=content.split("<br clear=\"none\"/>");
		if (str.length>0) {
			List<UserData> list=new ArrayList<UserData>();
			UserDisk userDisk=new UserDisk();
			for (int i =9; i < str.length; i++) {
				String line=str[i];
				String data=line;
				data = getStr(line, data);
				if (data.contains("diskID")) {
					String[] datas=data.split("=");
					userDisk.setDiskID(datas[1].trim());
				}
				if (data.contains("diskPWD")) {
					String[] datas=data.split("=");
					userDisk.setDiskPWD(datas[1].trim());
				}
				if (data.contains("userCounts")) {
					String[] datas=data.split("=");
					userDisk.setUserCounts(datas[1].trim());
				}
				if (data.contains("userName")) {
					UserData userData=new UserData();
					String[] datas=data.split("=");
					userData.setUserName(datas[1].trim());
					line=str[i+1];
					data=line;
					data =getStr(line, data);
					datas=data.split("=");
					userData.setSpaceName(datas[1].trim());
					list.add(userData);
				}
			}
			userDisk.setUserDataList(list);
			System.out.println(userDisk.getDiskID());
			System.out.println(userDisk.getDiskPWD());
			System.out.println(userDisk.getUserCounts());
			System.out.println(userDisk.getUserDataList().size());
			for (UserData userData : userDisk.getUserDataList()) {
			System.out.println(userData.getUserName());	
			System.out.println(userData.getSpaceName());	
			}
		}
	}

private String getStr(String line, String data) {
	if (line.contains("<div>")) {
		String[] str2=line.split("<div>");
		if (StringUtils.isNotEmpty(str2[0])) {
			data=str2[0];
		}else{
			data=str2[1];
		}
	}
	if (line.contains("</div>")) {
		data=line.substring(0,line.indexOf("</div>"));
	}
	return data;
}


}
