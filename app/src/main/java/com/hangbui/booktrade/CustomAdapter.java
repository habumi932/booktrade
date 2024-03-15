package com.hangbui.booktrade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    private Context context;
    private List<Book> books;

    public CustomAdapter(Context context, List<Book> books){
        this.context = context;
        this.books = books;
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_books_row, parent, false);
        }
        Book thisBook = books.get(position);

        TextView bookName = convertView.findViewById(R.id.textView_book_name);
        TextView authors = convertView.findViewById(R.id.textView_authors);
        TextView genre = convertView.findViewById(R.id.textView_genre);

        bookName.setText(thisBook.getName());
        authors.setText(thisBook.getAuthors());
        genre.setText(thisBook.getGenre());

        return convertView;
    }

}

