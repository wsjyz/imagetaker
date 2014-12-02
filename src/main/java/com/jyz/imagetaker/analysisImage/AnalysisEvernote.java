package com.jyz.imagetaker.analysisImage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.thrift.TException;
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
import org.apache.log4j.Logger;

public class AnalysisEvernote {

    protected Logger logger = Logger.getLogger(AnalysisEvernote.class);

    // 去这里申请一Token https://sandbox.evernote.com/api/DeveloperToken.action
	private static final String AUTH_TOKEN = "S=s1:U=8fee5:E=1513f302daa:C=149e77efea0:P=1cd:A=en-devtoken:V=2:H=c51eaf9567047bc98b3eccc0ded28070";

    public static Map<String,String> confMap;

	private UserStoreClient userStore;
	private NoteStoreClient noteStore;

    private static AnalysisEvernote instance;

    public static AnalysisEvernote getInstance(){
        if(instance == null){
            instance = new AnalysisEvernote();
        }
        return instance;
    }
	/**
	 * 初始UserStore and NoteStore 客户
	 */
	private AnalysisEvernote() {

		EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX,
                AUTH_TOKEN);
		ClientFactory factory = new ClientFactory(evernoteAuth);
        try {
            userStore = factory.createUserStoreClient();
        } catch (TTransportException e) {
            e.printStackTrace();
        }

        boolean versionOk = false;
        try {
            versionOk = userStore.checkVersion("Evernote EDAMDemo (Java)",
                    com.evernote.edam.userstore.Constants.EDAM_VERSION_MAJOR,
                    com.evernote.edam.userstore.Constants.EDAM_VERSION_MINOR);
        } catch (TException e) {
            e.printStackTrace();
        }
        if (!versionOk) {
            logger.error("不兼容的Evernote客户端协");
		}
        logger.info("读取配置信息！");
		// 设置 NoteStore 客户
        try {
            noteStore = factory.createNoteStoreClient();
        } catch (EDAMUserException e) {
            if (e.getErrorCode() == EDAMErrorCode.AUTH_EXPIRED) {
                logger.error("授权的Token已经过期");
            } else if (e.getErrorCode() == EDAMErrorCode.INVALID_AUTH) {
                logger.error("无效的授权Token");
            } else if (e.getErrorCode() == EDAMErrorCode.QUOTA_REACHED) {
                logger.error("无效的授权Token");
            } else {
                logger.error("错误" + e.getErrorCode().toString()
                        + " 参数" + e.getParameter());
            }
        } catch (EDAMSystemException e) {
            logger.error("系统错误" + e.getErrorCode().toString());
        } catch (TException e) {
            logger.error("网络错误" + e.getMessage());
        }
    }

	/**
	 * 获取并显示用户的笔记列表
	 */
	public Map<String, String> listNotes() {

        confMap = new HashMap<String, String>();
        try {
		// 列出用户的Notes
		// 首先，获取一个笔记本的列
		List<Notebook> notebooks = noteStore.listNotebooks();

		for (Notebook notebook : notebooks) {
			// 然后，搜索笔记本中前100个笔记并按创建日期排
			NoteFilter filter = new NoteFilter();
			filter.setNotebookGuid(notebook.getGuid());
			filter.setOrder(NoteSortOrder.CREATED.getValue());
			filter.setAscending(true);
			NoteList noteList = noteStore.findNotes(filter, 0, 10000);
			List<Note> notes = noteList.getNotes();
			for (Note note : notes) {
				Note fullNote = noteStore.getNote(note.getGuid(), true, true,
						false, false);
				getDataByString(confMap, fullNote.getContent());
			}
		}
        } catch (EDAMUserException e) {
            if (e.getErrorCode() == EDAMErrorCode.AUTH_EXPIRED) {
                logger.error("授权的Token已经过期");
            } else if (e.getErrorCode() == EDAMErrorCode.INVALID_AUTH) {
                logger.error("无效的授权Token");
            } else if (e.getErrorCode() == EDAMErrorCode.QUOTA_REACHED) {
                logger.error("无效的授权Token");
            } else {
                logger.error("错误" + e.getErrorCode().toString()
                        + " 参数" + e.getParameter());
            }
        } catch (EDAMSystemException e) {
            logger.error("系统错误" + e.getErrorCode().toString());
        } catch (TException e) {
            logger.error("网络错误" + e.getMessage());
        }catch(EDAMNotFoundException e){
            e.printStackTrace();
        }
		return confMap;
	}

	private void getDataByString(Map<String, String> map, String content) {
		String[] str = content.split("<br clear=\"none\"/>");
		if (str.length > 0) {
			for (int i = 9; i < str.length; i++) {
				String line = str[i];
				String data = line;
				data = getStr(line, data);
				if (StringUtils.isNotEmpty(data)) { 
					String[] datas = data.split("=");
					map.put(datas[0].trim(),datas[1].trim());
				}
			}
		}
	}

	private String getStr(String line, String data) {
		if (line.contains("<div>")) {
			String[] str2 = line.split("<div>");
			if (StringUtils.isNotEmpty(str2[0])) {
				data = str2[0];
			} else {
				data = str2[1];
			}
		}
		if (line.contains("</div>")) {
			data = line.substring(0, line.indexOf("</div>"));
		}
		return data;
	}

}
