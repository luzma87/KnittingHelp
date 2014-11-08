package com.lzm.KnittingHelp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.lzm.KnittingHelp.MainActivity;
import com.lzm.KnittingHelp.R;
import com.lzm.KnittingHelp.activities.PatternInfoActivity;
import com.lzm.KnittingHelp.adapters.PatternsListAdapter;
import com.lzm.KnittingHelp.db.Pattern;
import com.lzm.KnittingHelp.utils.Utils;

import java.util.List;

/**
 * Created by DELL on 05/11/2014.
 */
public class PatternsListFragment extends MasterFragment {

    ListView listView;
    PatternsListAdapter adapter;
    List<Pattern> patterns;

    public final static String PATTERN_ID_MESSAGE = "com.lzm.KnittingHelp.PATTERN";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.patterns_list_layout, container, false);

        Utils.hideSoftKeyboard(context);

        listView = (ListView) view.findViewById(R.id.main_list_patterns);
        registerForContextMenu(listView);
        loadPatterns();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Pattern selected = patterns.get(pos);

                Intent intent = new Intent(context, PatternInfoActivity.class);
                intent.putExtra(PATTERN_ID_MESSAGE, selected.id);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                overridePendingTransition(R.anim.activity_push_up_in, R.anim.push_up_out);
//                context.overridePendingTransition(R.anim.activity_push_up_in, R.anim.activity_push_up_out);
                startActivity(intent);
            }
        });
        return view;
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = context.getMenuInflater();
        inflater.inflate(R.menu.patterns_list_context_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final int positionToRemove = info.position;
        final Pattern selected = patterns.get(positionToRemove);

        switch (item.getItemId()) {
            case R.id.patterns_list_context_menu_delete:

                selected.delete(context);
                patterns.remove(positionToRemove);
                adapter.notifyDataSetChanged();

                Utils.vibrate(context);
                Utils.toast(getString(R.string.pattern_deleted), context);
                return true;
            case R.id.patterns_list_context_menu_edit:
                ((CreatePatternFragment) context.fragments.get(context.CREATE_POS)).setData(selected.id);
                System.out.println("?????????????????????????????????????????????? " + selected.id);
                context.selectTab(context.CREATE_POS);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void loadPatterns() {
        patterns = Pattern.list(context);
        if (adapter != null) {
            adapter.clear();
            adapter.addAll(patterns);
            adapter.notifyDataSetChanged();
        } else {
            adapter = new PatternsListAdapter(context, patterns);
            listView.setAdapter(adapter);
        }
    }
}