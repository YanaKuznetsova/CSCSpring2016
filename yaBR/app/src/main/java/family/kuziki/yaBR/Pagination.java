package family.kuziki.yaBR;

/**
 * Class providing pagination of the book content
 */

import android.text.Layout;
import android.text.StaticLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class Pagination {
    private final ArrayList<CharSequence> pages;
    private final CharSequence source;
    private final TextView textView;
    private int currentPage;

    public Pagination(CharSequence source, TextView view) {
        this.source = source;
        this.textView = view;
        pages = new ArrayList<>();
        currentPage = 0;
    }

    public CharSequence getPage() {
        return pages.get(currentPage);
    }

    // display next page
    public void nextPage() {
        currentPage = Math.min(currentPage+1, pages.size()-1);
    }

    // display previous page
    public void previousPage() {
        currentPage = Math.max(currentPage-1, 0);
    }

    // do pagination
    public void layout(Task t) {
        int width = textView.getWidth() - textView.getPaddingLeft() - textView.getPaddingRight();
        int height = textView.getHeight() - textView.getPaddingTop() - textView.getPaddingBottom();
        StaticLayout staticLayout = new StaticLayout(source, textView.getPaint(), width, Layout.Alignment.ALIGN_NORMAL, textView.getLineSpacingMultiplier(), textView.getLineSpacingExtra(), textView.getIncludeFontPadding());
        int lineCount = staticLayout.getLineCount();

        CharSequence text = staticLayout.getText();
        int startPos = 0;
        int curHeight = height;

        int oldPercent = -1;
        for (int i = 0; i < lineCount; i++) {
            if (curHeight < staticLayout.getLineBottom(i)) {
                pages.add(text.subSequence(startPos, staticLayout.getLineStart(i)));
                curHeight = staticLayout.getLineTop(i) + height;
                startPos = staticLayout.getLineStart(i);
            }
            if (i == lineCount - 1) {
                pages.add(text.subSequence(startPos, staticLayout.getLineEnd(i)));
            }
            int newPercent = (int)(1.0*i/lineCount * 100);
            if (newPercent != oldPercent) {
                t.action(newPercent);
                oldPercent = newPercent;
            }
        }
    }

    public interface Task {
        void action(int num);
    }
}
