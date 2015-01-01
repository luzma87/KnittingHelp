package com.lzm.KnittingHelp.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.*;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import com.lzm.KnittingHelp.MainActivity;
import com.lzm.KnittingHelp.R;
import com.lzm.KnittingHelp.activities.PatternEditActivity;
import com.lzm.KnittingHelp.activities.PatternInfoActivity;
import com.lzm.KnittingHelp.activities.PatternViewActivity;
import com.lzm.KnittingHelp.adapters.PatternsListAdapter;
import com.lzm.KnittingHelp.db.Pattern;
import com.lzm.KnittingHelp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 05/11/2014.
 */
public class PatternsListFragment extends MasterFragment {

    ListView listView;
    PatternsListAdapter adapter;
    List<Pattern> patterns;

    ActionMode myActionMode;

    public final static String PATTERN_ID_MESSAGE = "com.lzm.KnittingHelp.PATTERN";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //log.e("LZM", "onCreateView");
        context = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.patterns_list_layout, container, false);

        Utils.hideSoftKeyboard(context);

        listView = (ListView) view.findViewById(R.id.main_list_patterns);
        // esto es para el context menu flotante
//        registerForContextMenu(listView);
        // fin context menu flotante

        // esto es para el contextual action bar
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(modeListener);
        // fin contextual action bar
//        loadPatterns();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Pattern selected = patterns.get(pos);

//                if (selected.currentSeccionId > 0) {
                Intent intent = new Intent(context, PatternViewActivity.class);
                intent.putExtra(PATTERN_ID_MESSAGE, selected.id);
                startActivity(intent);
//                } else {
//                    Intent intent = new Intent(context, PatternEditActivity.class);
//                    intent.putExtra(PATTERN_ID_MESSAGE, selected.id);
//                    startActivity(intent);
//                }

            }
        });
        return view;
    }

    // Esto es para el context menu flotante
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
                context.selectTab(context.CREATE_POS);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    // fin del context menu flotante

    // Esto es para el contextual action bar
    private AbsListView.MultiChoiceModeListener modeListener = new AbsListView.MultiChoiceModeListener() {

        //        private int selCount = 0;
//        ArrayList<Integer> posList = new ArrayList<Integer>();
        ArrayList<Pattern> patternArrayList = new ArrayList<Pattern>();

        public void resetCounters() {
//            selCount = 0;
//            posList = new ArrayList<Integer>();
            patternArrayList = new ArrayList<Pattern>();
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.patterns_list_contextual_action_bar, menu);
            myActionMode = mode;
//            mode.setTitle("Action Bar!");
//            mode.setSubtitle("Subtitle");
            return true;
        }

        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            if (checked) {
//                selCount++;
//                posList.add(position);
                patternArrayList.add(patterns.get(position));
            } else {
//                selCount--;
//                posList.remove(position);851
                patternArrayList.remove(patterns.get(position));
            }
            int selCount = patternArrayList.size();
            String strSelected = Utils.getPluralResourceByName(context, "global_n_selected", selCount, "" + selCount);
            mode.setTitle(strSelected);
            mode.invalidate();  // Add this to Invalidate CAB
//            itemSelectedPosition = position;
//            listView.setItemChecked(position, checked);
//            //log.e("LZM", "onItemCheckedStateChanged::: selCount=" + selCount + "   id=" + id + "   checked=" + checked + "   selected=" + idList);
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // Used for updates to CAB after invalidate() request
            if (patternArrayList.size() == 1) {
                MenuItem item = menu.findItem(R.id.patterns_list_cab_edit);
                item.setVisible(true);

                if (patternArrayList.get(0).fechaCreacion != null) {
                    item = menu.findItem(R.id.patterns_list_cab_pic);
                    item.setVisible(true);
                }
            } else {
                MenuItem item = menu.findItem(R.id.patterns_list_cab_edit);
                item.setVisible(false);

                item = menu.findItem(R.id.patterns_list_cab_pic);
                item.setVisible(false);
            }
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            //log.e("LZM", "onItemCheckedStateChanged::: selCount=" + pa + "   posList=" + posList);
//            final int positionToRemove = itemSelectedPosition;
//            //log.e("LZM", "onActionItemClicked::: " + itemSelectedPosition);
//            final Pattern selected = patterns.get(positionToRemove);
//
            final ActionMode m = mode;
            switch (item.getItemId()) {
                case R.id.patterns_list_cab_edit:
                    if (patternArrayList.size() == 1) {
                        Pattern selected = patternArrayList.get(0);
                        ((CreatePatternFragment) context.fragments.get(context.CREATE_POS)).setData(selected.id);
                        resetCounters();
                        mode.finish();
                        context.selectTab(context.CREATE_POS);
                        return true;
                    }
                    return false;
                case R.id.patterns_list_cab_delete:
                    int selCount = patternArrayList.size();
                    String strDeleteContent = Utils.getPluralResourceByName(context, "delete_n_patterns_dlg_message", selCount, "" + selCount);
                    String strDeleteTitle = Utils.getPluralResourceByName(context, "delete_n_patterns_dlg_title", selCount, "" + selCount);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    // Chain together various setter methods to set the dialog characteristics
                    builder.setMessage(strDeleteContent)
                            .setTitle(strDeleteTitle);

                    // Add the buttons
                    builder.setPositiveButton(R.string.global_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            while (patternArrayList.size() > 0) {
                                Pattern selected = patternArrayList.get(0);
                                selected.delete(context);
                                patterns.remove(selected);
                                patternArrayList.remove(selected);
                            }
                            loadPatterns();
                            m.finish();
                        }
                    });
                    builder.setNegativeButton(R.string.global_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    // Set other dialog properties

                    // Create the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                default:
                    return false;
            }
        }

        public void onDestroyActionMode(ActionMode mode) {
            resetCounters();
            myActionMode = null;
            // do nothing
        }

    };
    // fin del contextual action bar

    public void loadPatterns() {
//        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> RELOAD PATTERNS !!!!!!!!!!!!!!!!!!");
        patterns = Pattern.list(context);
//        if (adapter != null) {
//            adapter.clear();
//            adapter.addAll(patterns);
//            adapter.notifyDataSetChanged();
//        } else {
        adapter = null;
        adapter = new PatternsListAdapter(context, patterns);
        listView.setAdapter(adapter);
//        }
    }

    public void onTabUnselected() {
        if (myActionMode != null)
            myActionMode.finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        //log.e("LZM", "onResume");
        loadPatterns();
        try {
            myActionMode.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            //log.e("LZM", "onCreate FIRST TIME");
        } else {
            //log.e("LZM", "onCreate SUBSEQUENT TIME");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //log.e("LZM", "onActivityCreated");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //log.e("LZM", "onSaveInstanceState");
    }

    @Override
    public void onStart() {
        super.onStart();
        //log.e("LZM", "onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        //log.e("LZM", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        //log.e("LZM", "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //log.e("LZM", "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //log.e("LZM", "onDetach");
    }
}