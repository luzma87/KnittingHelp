package com.lzm.KnittingHelp.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import com.lzm.KnittingHelp.MainActivity;
import com.lzm.KnittingHelp.R;
import com.lzm.KnittingHelp.activities.PatternInfoActivity;
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
//                posList.remove(position);
                patternArrayList.remove(patterns.get(position));
            }
            int selCount = patternArrayList.size();
            String strSelected = Utils.getPluralResourceByName(context, "global_n_selected", selCount, "" + selCount);
            mode.setTitle(strSelected);
            mode.invalidate();  // Add this to Invalidate CAB
//            itemSelectedPosition = position;
//            listView.setItemChecked(position, checked);
//            Log.d("LZM", "onItemCheckedStateChanged::: selCount=" + selCount + "   id=" + id + "   checked=" + checked + "   selected=" + idList);
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // Used for updates to CAB after invalidate() request
            if (patternArrayList.size() == 1) {
                MenuItem item = menu.findItem(R.id.patterns_list_cab_edit);
                item.setVisible(true);
                return true;
            } else {
                MenuItem item = menu.findItem(R.id.patterns_list_cab_edit);
                item.setVisible(false);
                return true;
            }
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            Log.d("LZM", "onItemCheckedStateChanged::: selCount=" + pa + "   posList=" + posList);
//            final int positionToRemove = itemSelectedPosition;
//            Log.d("LZM", "onActionItemClicked::: " + itemSelectedPosition);
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
//        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> RELOAD!!!!!!!!!!!!!!!!!!");
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

    @Override
    public void onResume() {
        super.onResume();
        loadPatterns();
    }

    public void onTabUnselected() {
        if (myActionMode != null)
            myActionMode.finish();
    }
}