package net.jotorren.microservices.forum.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import net.jotorren.microservices.tx.ChangeStateJpaListener;

@Entity
public class Forum extends ChangeStateJpaListener implements Serializable {

    private static final long serialVersionUID = -8739470216693425165L;

    @Id
    @Column(nullable = false)
    private String forumId;

    @Column(nullable = false)
    private String topicName;

    @Column
    private String topicCategory;

    @Column(nullable = false)
    private String subjectId;

    public String getForumId() {
        return forumId;
    }

    public void setForumId(String forumId) {
        this.forumId = forumId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicCategory() {
        return topicCategory;
    }

    public void setTopicCategory(String topicCategory) {
        this.topicCategory = topicCategory;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }
}
