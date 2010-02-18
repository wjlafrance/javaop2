/*
 * Created on Dec 13, 2004 By iago
 */
package com.javaop.util;

import com.javaop.constants.PriorityConstants;


/**
 * @author iago
 * 
 */
public class ChatMessage implements PriorityConstants, Comparable<ChatMessage>
{
    private String      text;
    private int         priority;
    private long        time;
    static private long count = 0;

    public ChatMessage(String text)
    {
        this(text, PRIORITY_NORMAL);
    }

    public ChatMessage(String text, int priority)
    {
        this.text = text;
        this.priority = priority;
        this.time = count++;
    }

    public String getText()
    {
        return text;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String toString()
    {
        return text;
    }

    /** Throws a ClassCastException if o isn't a ChatMessage */
    public int compareTo(ChatMessage o)
    {
        if (o == this)
            return 0;

        if (((ChatMessage) o).priority == priority)
            return time > ((ChatMessage) o).time ? 1 : -1;

        return priority > ((ChatMessage) o).priority ? -1 : 1;
    }
}
