package family.kuziki.yaBR;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Admin on 018 18.05.16.
 */
public class Pagination {
    private final ArrayList<CharSequence> pages;
    private final CharSequence source;
    private final TextView textView;

    public Pagination(CharSequence source, TextView view) {
        this.source = source;
        this.textView = view;
        pages = new ArrayList<>();

    }

    private void layout() {
        int width = textView.getWidth() - textView.getPaddingLeft() - textView.getPaddingRight();
        int height = textView.getHeight() - textView.getPaddingTop() - textView.getPaddingBottom();
        StaticLayout staticLayout = new StaticLayout(source, textView.getPaint(), width, Layout.Alignment.ALIGN_NORMAL, textView.getLineSpacingMultiplier(), textView.getLineSpacingExtra(), textView.getIncludeFontPadding());
        int lineCount = staticLayout.getLineCount();

        CharSequence text = staticLayout.getText();
        int startPos = 0;
        int curHeight = height;

        for (int i = 0; i < lineCount; i++) {
            if (curHeight < staticLayout.getLineBottom(i)) {
                pages.add(text.subSequence(startPos, staticLayout.getLineStart(i)));
                curHeight = staticLayout.getLineTop(i) + height;
                startPos = staticLayout.getLineStart(i);
            }
            if (i == lineCount - 1) {
                pages.add(text.subSequence(startPos, staticLayout.getLineEnd(i)));
            }
        }
    }
}
