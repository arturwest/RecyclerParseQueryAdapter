package west.code.recyclerparsequeryadapterexample.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import west.code.recyclerparsequeryadapterexample.R;
import west.code.recyclerparsequeryadapterexample.adapters.RecyclerParseQueryAdapter;
import west.code.recyclerparsequeryadapterexample.helpers.Const;


/**
 * Created by Artco on 23.07.2016.
 */
public class MainFragment extends Fragment {

    private RecyclerView listView;
    int pastVisibleItems, visibleItemCount, totalItemCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main,container,false);
        listView = (RecyclerView) v.findViewById(android.R.id.list);

        final RecyclerParseQueryAdapter adapter = new RecyclerParseQueryAdapter(getActivity(), Const.VIEW_TYPE_CITY_ITEM,"City","name");
        final LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        listView.setLayoutManager(manager);
        listView.setAdapter(adapter);
        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = manager.getChildCount();
                    totalItemCount = manager.getItemCount();
                    pastVisibleItems = manager.findFirstVisibleItemPosition();

                    if ( (visibleItemCount + pastVisibleItems) >= totalItemCount)
                    {
                        adapter.loadNextPage();
                    }

                }
            }
        });



        return v;
    }


}
