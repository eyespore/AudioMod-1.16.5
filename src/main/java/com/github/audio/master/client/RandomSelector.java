package com.github.audio.master.client;

import com.github.audio.sound.AudioSound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.LinkedList;

@OnlyIn(Dist.CLIENT)
public class RandomSelector extends AudioSelector {

    private final AudioSelector sel;
    private boolean isRandomList = false;

    public RandomSelector(AudioSelector sel) {
        super(sel.channel);
        this.sel = sel;
    }

    public void inRandom(Enum<Position> at) {
        sel.currentList = getRandomList(at);
        if (at == Position.HEAD) sel.pointer = 0;
        if (at == Position.TAIL) sel.pointer = sel.channel.getSize() - 1;
    }

    public void outRandom() {
        LinkedList<Integer> temList = new LinkedList<>();
        for (int i = 0; i < sel.channel.getSize(); i++) temList.add(i, i);
        sel.currentList = temList;
        pointer = sel.channel.getList().indexOf(sel.getCurrent());
    }

    public AudioSound randNext() {
        if (!isRandomList) {
            inRandom(Position.HEAD);
            isRandomList = true;
        }
        sel.pointer ++;
        if (sel.pointer > sel.channel.getSize() - 1) inRandom(Position.HEAD);
        return sel.channel.getList().get(sel.currentList.get(sel.pointer));
    }

    public AudioSound randLast() {
        if (!isRandomList) {
            inRandom(Position.TAIL);
            isRandomList = true;
        }
        sel.pointer --;
        if (sel.pointer < 0) inRandom(Position.TAIL);
        return sel.channel.getList().get(sel.currentList.get(sel.pointer));
    }

    /**
     * @return the random list summoned from the AudioContext that given to the method.
     * @Description: Method that used for initialize the sound index array while player change the mp3 mode into random, immediately mp3's mode
     * is turn into random, this method will be called to construct a new array.
     */
    public LinkedList<Integer> getRandomList(Enum<Position> at) {
        LinkedList<Integer> temList = new LinkedList<>();
        for (int i = 0; i < sel.channel.getSize(); i++) temList.add(i, i);

        LinkedList<Integer> randomList = new LinkedList<>(temList);
        Collections.shuffle(randomList);

        int removeIndex = sel.currentList.get(pointer);
        randomList.removeIf((i) -> i == removeIndex);
        if (at == Position.HEAD) randomList.addFirst(removeIndex);
        if (at == Position.TAIL) randomList.addLast(removeIndex);
        return randomList;
    }

    @Override
    public AudioSound next() {
        if (isRandomList) {
            outRandom();
            isRandomList = false;
        }
        return sel.next();
    }

    @Override
    public AudioSound last() {
        if (isRandomList) {
            outRandom();
            isRandomList = false;
        }
        return sel.last();
    }

    @Override
    public AudioSound getNext() {
        return sel.getNext();
    }

    @Override
    public AudioSound getLast() {
        return sel.getLast();
    }

    @Override
    public AudioSound getCurrent() {
        return sel.getCurrent();
    }

    @Override
    public void reload() {
        sel.reload();
    }

    private enum Position {
        HEAD, TAIL;
    }


}
