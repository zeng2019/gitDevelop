package cn.edu.haust.litepaltest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button createDatabase = (Button) findViewById(R.id.create_database);
        createDatabase.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
              Connector.getDatabase();
              Log.d("MainActivity","database was created.");
            }
        });

       Button addData = (Button) findViewById(R.id.add_data);
       addData.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               Book book = new Book();
               book.setName("The Da Vinci Code");
               book.setAuthor("Dan Brown");
               book.setPages(454);
               book.setPrice(16.96);
               book.setPress("Unknow");
               book.save();
               Log.d("MainActivity","new data was added, name is: "+ book.getName());
           }
       });

       Button updateData = (Button) findViewById(R.id.update_data);
       updateData.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               Book book = new Book();
               book.setName("The Lost Symbol new");
               book.setAuthor("Dan Brown new");
               book.setPages(510);
               book.setPrice(19.91);
               book.setPress("test");
               book.save();
               book.setPrice(10.99);
               book.save();
//update method two:
/*               Book book = new Book();
               book.setPrice(14.95);
               book.setPress("Anchor");
               book.updateAll("name = ? and author = ?", "The Lost Symbol","Dan Brown");*/
               Log.d("MainActivity","data was updated, name is: "+ book.getName());
           }
       });

       Button deleteData = (Button) findViewById(R.id.delete_data);
       deleteData.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               DataSupport.deleteAll(Book.class, "price < ?","15");
           }
       });

       Button queryData  = (Button) findViewById(R.id.query_data);
       queryData.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               List<Book> books = DataSupport.findAll(Book.class);
               for (Book book: books) {
                   Log.d("MainActivity","book name is: "+book.getName());
                   Log.d("MainActivity","book author is " + book.getAuthor());
                   Log.d("MainActivity","book pages is  " + book.getPages());
                   Log.d("MainActivity","book price is "+ book.getPrice());
                   Log.d("MainActivity","book press is" + book.getPress());
               }
           }
       });
    }
}