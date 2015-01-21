package com.ksanur.questionreader;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Sundara on 1/19/15.
 */
public class QuestionGUI {
    private JCheckBox physicsCheckBox;
    private JCheckBox biologyCheckBox;
    private JCheckBox earthSpaceCheckBox;
    private JCheckBox mathCheckBox;
    private JCheckBox energyCheckBox;
    private JCheckBox generalCheckBox;
    private JCheckBox multipleChoiceCheckBox;
    private JCheckBox shortAnswerCheckBox;
    private JButton getQuestionButton;
    private JTextArea questionTextArea;
    private JPanel root;
    private JCheckBox chemistryCheckBox;


    private Question q;

    private int qIdx = 0;

    public QuestionGUI() {
        getQuestionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(q==null) {

                    while(q==null) {
                        if(qIdx<QuestionReader.questions.size()) {
                            Question n = QuestionReader.questions.get(qIdx);

                            if (getCheckBox(n.getCategory()).isSelected() && getCheckBox(n.isMultipleChoice()).isSelected()) {
                                q = n;
                                questionTextArea.setText(q.getCategory().getName());
                                questionTextArea.append("\n"+((q.isMultipleChoice())?"Multiple Choice":"Short Answer"));
                                questionTextArea.append("\n"+q.getQuestion()+"\n");
                                if(q.isMultipleChoice()) {
                                    questionTextArea.append("\n"+q.getW());
                                    questionTextArea.append("\n"+q.getX());
                                    questionTextArea.append("\n"+q.getY());
                                    questionTextArea.append("\n"+q.getZ());
                                }
                                getQuestionButton.setText("Get Answer");
                            }
                            qIdx++;
                        }
                        else {
                            questionTextArea.setText("Out of questions!!! :(");
                            break;
                        }
                    }


                    /*q = QuestionReader.questions.get(new Random().nextInt(QuestionReader.questions.size()));
                    questionTextArea.setText(q.getQuestion()+"\n");
                    if(q.getType()== Question.AnswerType.MULTIPLE_CHOICE) {
                        questionTextArea.append("\n"+q.getW());
                        questionTextArea.append("\n"+q.getX());
                        questionTextArea.append("\n"+q.getY());
                        questionTextArea.append("\n"+q.getZ());
                    }
                    getQuestionButton.setText("Get Answer");*/
                }
                else {
                    questionTextArea.append("\n\n---------------");
                    questionTextArea.append("\n\n"+q.getAnswer());
                    getQuestionButton.setText("Get Question");
                    q = null;
                }
            }
        });
    }

    public JTextArea getQuestionTextArea() {
        return questionTextArea;
    }

    public boolean isMC() {
        return multipleChoiceCheckBox.isSelected();
    }

    public boolean isSA() {
        return shortAnswerCheckBox.isSelected();
    }


    public JPanel getPanel() {
        return root;
    }


    public JCheckBox getCheckBox(boolean multipleChoice) {
        return (multipleChoice)?multipleChoiceCheckBox:shortAnswerCheckBox;
    }

    public JCheckBox getCheckBox(Question.Category c) {
        switch (c) {
            case GENERAL:
                return generalCheckBox;
            case EARTH_SPACE:
                return earthSpaceCheckBox;
            case LIFE:
                return biologyCheckBox;
            case PHYSICAL:
                return physicsCheckBox;
            case MATH:
                return mathCheckBox;
            case ENERGY:
                return energyCheckBox;
            case CHEMISTRY:
                return chemistryCheckBox;
        }
        return null;
    }
}
