package family.kuziki.yaBR;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

public class LibraryAdapter extends BaseAdapter {
    private static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";
//    private static final String QUERY_URL = "http://openlibrary.org/search.json?q=";
//    private static final String TITLE = "title";
//    private static final String AUTHOR = "author";
//    private static final String COVER = "cover";

    Context context;
    LayoutInflater inflater;

    private class ViewHolder {
        public ImageView thumbnailImageView;
        public TextView titleTextView;
        public TextView authorTextView;
    }

    public LibraryAdapter(Context context, LayoutInflater inflater) {
        this.context = context;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return Library.getInstance().getLibraryItems().size();
    }

    @Override
    public Object getItem(int position) {
        return Library.getInstance().getLibraryItems().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_book, null);
            holder = new ViewHolder();
            holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.img_thumbnail);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.text_title);
            holder.authorTextView = (TextView) convertView.findViewById(R.id.text_author);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        LibraryItem currentItem = (LibraryItem) getItem(position);
        if (currentItem.getCover() != null) {
            String imageID = currentItem.getCover();
            String imageURL = IMAGE_URL_BASE + imageID + "-S.jpg";
            Picasso.with(context).load(imageURL).placeholder(R.drawable.ic_books).into(holder.thumbnailImageView);
        } else {
            holder.thumbnailImageView.setImageResource(R.drawable.ic_books);
        }
        String bookTitle = "";
        String authorName = "";
        if (currentItem.getTitle()!= null) {
            bookTitle = currentItem.getTitle();
        }
        if (currentItem.getAuthor()!= null) {
            authorName = currentItem.getAuthor();
        }
        holder.titleTextView.setText(bookTitle);
        holder.authorTextView.setText(authorName);
        return convertView;
    }

    public void updateData() {
        notifyDataSetChanged();
    }
}
