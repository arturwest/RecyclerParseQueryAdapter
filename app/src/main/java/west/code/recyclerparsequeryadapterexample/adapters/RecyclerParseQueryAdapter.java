package west.code.recyclerparsequeryadapterexample.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.ArrayList;
import java.util.List;

import west.code.recyclerparsequeryadapterexample.R;
import west.code.recyclerparsequeryadapterexample.helpers.Const;


/**
 * Created by Artco on 23.07.2016.
 */
public class RecyclerParseQueryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public  interface OnQueryLoadListener<ParseObject> {
        public void onLoading();

        public void onLoaded(List<ParseObject> objects, Exception e);
    }


    private List<OnQueryLoadListener<ParseObject>> onQueryLoadListeners =
            new ArrayList<>();


    int viewType;
    String className;
    ArrayList<ParseObject> items = new ArrayList<>();
    Context context;
    private ParseQueryAdapter.QueryFactory<ParseObject> queryFactory;

    private int objectsPerPage = 25;
    private boolean paginationEnabled = true;
    private boolean hasNextPage = true;
    private int currentPage = 0;
    private List<List<ParseObject>> objectPages = new ArrayList<>();

    public RecyclerParseQueryAdapter(Context context, int viewType, final String className, final String orderBy) {
        this.viewType = viewType;
        this.context = context;
        this.className = className;

        queryFactory = new ParseQueryAdapter.QueryFactory<ParseObject>() {
            @Override
            public ParseQuery<ParseObject> create() {
                ParseQuery<ParseObject> query = ParseQuery.getQuery(className);
                query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
                query.orderByAscending(orderBy);
                return query;
            }
        };

        loadObjects(currentPage);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType){
            case Const.VIEW_TYPE_CITY_ITEM:
                v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.city_item, parent, false);
                return new CityViewHolder(v);


            case Const.VIEW_TYPE_CATEGORY_ITEM:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.categories_item, parent, false);
                return new CategoriesViewHolder(v);


            default:
                return null;
        }




    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ParseObject object = getItem(position);


        switch (viewType){
            case Const.VIEW_TYPE_CITY_ITEM:
                ((CityViewHolder)holder).cityName.setText(object.getString("name"));
                break;
            case Const.VIEW_TYPE_CATEGORY_ITEM:
                break;

            default:
                break;
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public ParseObject getItem(int position){
        return items.get(position);
    }

    public static class CategoriesViewHolder extends RecyclerView.ViewHolder {
        public ParseImageView image;
        public TextView text;
        public ImageView arrow;

        public CategoriesViewHolder(View v) {
            super(v);
            image = (ParseImageView) v.findViewById(R.id.categoriesImage);
            arrow = (ImageView)v.findViewById(R.id.categoriesArrow);
            text = (TextView)v.findViewById(R.id.categoriesName);
        }
    }

    public static class CityViewHolder extends RecyclerView.ViewHolder {
        public TextView cityName;

        public CityViewHolder(View v) {
            super(v);
            cityName = (TextView) v;
        }
    }



    private void loadObjects(final int page) {
        final ParseQuery<ParseObject> query = this.queryFactory.create();

        if (this.objectsPerPage > 0 && this.paginationEnabled) {
            this.setPageOnQuery(page, query);
        }

        this.notifyOnLoadingListeners();

        if (page >= objectPages.size()) {
            objectPages.add(page, new ArrayList<ParseObject>());
        }




        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> foundObjects, ParseException e) {


                if ((e != null) &&
                        ((e.getCode() == ParseException.CONNECTION_FAILED) ||
                                (e.getCode() != ParseException.CACHE_MISS))) {
                    hasNextPage = true;
                } else if (foundObjects != null) {

                    // Only advance the page, this prevents second call back from CACHE_THEN_NETWORK to
                    // reset the page.
                    if (page >= currentPage) {
                        currentPage = page;

                        // since we set limit == objectsPerPage + 1
                        hasNextPage = (foundObjects.size() > objectsPerPage);
                    }

                    if (paginationEnabled && foundObjects.size() > objectsPerPage) {
                        // Remove the last object, fetched in order to tell us whether there was a "next page"
                        foundObjects.remove(objectsPerPage);
                    }

                    List<ParseObject> currentPage = objectPages.get(page);
                    currentPage.clear();
                    currentPage.addAll(foundObjects);

                    syncObjectsWithPages();

                    // executes on the UI thread
                    notifyDataSetChanged();
                }

                notifyOnLoadedListeners(foundObjects, e);


            }
        });
    }
    public void loadNextPage() {
        if (items.size() == 0) {
            loadObjects(0);
        }
        else {
            loadObjects(currentPage + 1);
        }
    }

    public void setObjectsPerPage(int objectsPerPage) {
        this.objectsPerPage = objectsPerPage;
    }


    private void syncObjectsWithPages() {
        items.clear();
        for (List<ParseObject> pageOfObjects : objectPages) {
            items.addAll(pageOfObjects);
        }
    }

    protected void setPageOnQuery(int page, ParseQuery<ParseObject> query) {
        query.setLimit(this.objectsPerPage + 1);
        query.setSkip(page * this.objectsPerPage);
    }

    public void addOnQueryLoadListener(OnQueryLoadListener<ParseObject> listener) {
        this.onQueryLoadListeners.add(listener);
    }

    public void removeOnQueryLoadListener(OnQueryLoadListener<ParseObject> listener) {
        this.onQueryLoadListeners.remove(listener);
    }

    private void notifyOnLoadingListeners() {
        for (OnQueryLoadListener<ParseObject> listener : this.onQueryLoadListeners) {
            listener.onLoading();
        }
    }

    private void notifyOnLoadedListeners(List<ParseObject> objects, Exception e) {
        for (OnQueryLoadListener<ParseObject> listener : this.onQueryLoadListeners) {
            listener.onLoaded(objects, e);
        }
    }





}
