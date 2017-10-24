package com.xiuyukeji.plugin.translation;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.TextRange;
import com.xiuyukeji.plugin.translation.translator.impl.GoogleTranslator;
import org.apache.http.util.TextUtils;

/**
 * 谷歌翻译插件
 *
 * @author Created by jz on 2017/10/24 14:44
 */
public class GoogleTranslation extends AnAction {
    private long mLatestClickTime;
    private final GoogleTranslator mTranslator = new GoogleTranslator();

    public GoogleTranslation() {
        super(IconLoader.getIcon("/icons/translate.png"));
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        if (!isFastClick()) {
            getTranslation(e);
        }
    }

    private void getTranslation(AnActionEvent event) {
        Editor mEditor = event.getData(PlatformDataKeys.EDITOR);
        if (null == mEditor) {
            return;
        }
        SelectionModel model = mEditor.getSelectionModel();
        String selectedText = model.getSelectedText();
        if (TextUtils.isEmpty(selectedText)) {
            selectedText = getCurrentWords(mEditor);
            if (TextUtils.isEmpty(selectedText)) {
                return;
            }
        }
        String queryText = strip(addBlanks(selectedText));
        new Thread(new RequestRunnable(mTranslator, mEditor, queryText)).start();
    }

    private String getCurrentWords(Editor editor) {
        Document document = editor.getDocument();
        CaretModel caretModel = editor.getCaretModel();
        int caretOffset = caretModel.getOffset();
        int lineNum = document.getLineNumber(caretOffset);
        int lineStartOffset = document.getLineStartOffset(lineNum);
        int lineEndOffset = document.getLineEndOffset(lineNum);
        String lineContent = document.getText(new TextRange(lineStartOffset, lineEndOffset));
        char[] chars = lineContent.toCharArray();
        int start = 0, end = 0, cursor = caretOffset - lineStartOffset;

        if (!Character.isLetter(chars[cursor])) {
            return null;
        }

        for (int ptr = cursor; ptr >= 0; ptr--) {
            if (!Character.isLetter(chars[ptr])) {
                start = ptr + 1;
                break;
            }
        }

        int lastLetter = 0;
        for (int ptr = cursor; ptr < lineEndOffset - lineStartOffset; ptr++) {
            lastLetter = ptr;
            if (!Character.isLetter(chars[ptr])) {
                end = ptr;
                break;
            }
        }
        if (end == 0) {
            end = lastLetter + 1;
        }

        return new String(chars, start, end - start);
    }

    private String addBlanks(String str) {
        String temp = str.replaceAll("_", " ");
        if (temp.equals(temp.toUpperCase())) {
            return temp;
        }
        return temp.replaceAll("([A-Z]+)", " $0");
    }

    private String strip(String str) {
        return str.replaceAll("/\\*+", "")
                .replaceAll("\\*+/", "")
                .replaceAll("\\*", "")
                .replaceAll("//+", "")
                .replaceAll("\r\n", " ")
                .replaceAll("\\s+", " ");
    }

    private boolean isFastClick() {
        long time = System.currentTimeMillis();
        long timeD = time - mLatestClickTime;
        if (0 < timeD && timeD < (long) 1000) {
            return true;
        }
        mLatestClickTime = time;
        return false;
    }
}
