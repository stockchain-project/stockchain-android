package com.stockchain.bcapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.stockchain.cosmos.BoardInform;

import java.io.IOException;
import java.util.ArrayList;

public class MainBoardFragment extends Fragment {
    FragmentTransaction ft;
    RecyclerView boardRecyclerView;
    BoardAdapter boardAdapter;
    Fragment fg = this;

    int page=1;
    boolean flag=true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_board, container, false);
        MainActivity mainActivity = (MainActivity)getActivity();

        fg=this;
        boardRecyclerView = rootView.findViewById(R.id.boardRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        boardRecyclerView.setLayoutManager(layoutManager);
        final FragmentTransaction ft = getChildFragmentManager().beginTransaction();

        boardAdapter = new BoardAdapter(mainActivity);
        try {
            ArrayList<BoardInform> boardList = mainActivity.bd.listBoardPage(page);
            boardAdapter.setItems(boardList);
        } catch (IOException e) {}
        boardRecyclerView.setAdapter(boardAdapter);
        boardRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!boardRecyclerView.canScrollVertically(-1)) {
                    if (flag) {
                        try {
                            page=1;
                            ArrayList<BoardInform> boardList = mainActivity.bd.listBoardPage(page);
                            boardAdapter.setItems(boardList);
                            boardAdapter.notifyDataSetChanged();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        flag=false;
                    }
                } else if (!boardRecyclerView.canScrollVertically(1)) {
                    if(flag) {
                        try {
                            page++;
                            ArrayList<BoardInform> boardList = mainActivity.bd.listBoardPage(page);
                            boardAdapter.addItems(boardList);
                            boardAdapter.notifyItemRangeInserted(boardAdapter.items.size(), boardList.size());
                        } catch (IOException e) {
                            page--;
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    flag=true;
                }
            }
        });
        FloatingActionButton goBoardCreateButton = (FloatingActionButton) rootView.findViewById(R.id.goBoardCreatebutton) ;
        goBoardCreateButton.setOnClickListener(new onClickGoBoardCreateButton());

        return rootView;
    }

    class onClickGoBoardCreateButton implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            MainActivity mainActivity = (MainActivity)getActivity();
            BoardCreateFragment boardCreateFragment = new BoardCreateFragment();
            mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, boardCreateFragment).addToBackStack(null).commit();
        }
    }


    class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder>{
        ArrayList<BoardInform> items = new ArrayList<>();
        MainActivity mainActivity;

        public BoardAdapter(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @NonNull
        @Override
        public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View itemView = inflater.inflate(R.layout.board_inform, viewGroup, false);
            return new BoardViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull BoardViewHolder holder, int position) {
            BoardInform item = items.get(position);
            holder.setItem(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void addItem(BoardInform item){
            items.add(item);
        }

        public  void setItems(ArrayList<BoardInform> items){
            this.items = items;
        }
        public  void addItems(ArrayList<BoardInform> items){
            this.items.addAll(items);
        }

        public void setItem(int position, BoardInform item){
            items.set(position, item);
        }
        public BoardInform getItem(int position){
            return items.get(position);
        }

        public class BoardViewHolder extends RecyclerView.ViewHolder{
            TextView boardId;
            TextView boardUsername;
            TextView boardTitle;

            public BoardViewHolder(@NonNull View itemView) {
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = getAdapterPosition() ;
                        if (pos != RecyclerView.NO_POSITION) {
                            mainActivity.mainBoardInform = getItem(pos);
                            mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, mainActivity.boardReadFragment).addToBackStack(null).commit();
                        }
                    }
                });
                boardId = itemView.findViewById(R.id.boardId);
                boardUsername = itemView.findViewById(R.id.boardUsername);
                boardTitle = itemView.findViewById(R.id.boardTitle);

            }

            public void setItem(BoardInform item){
                boardId.setText(String.valueOf(item.getId()));
                boardUsername.setText(item.getUsername());
                boardTitle.setText(item.getTitle());
            }
        }
    }
}