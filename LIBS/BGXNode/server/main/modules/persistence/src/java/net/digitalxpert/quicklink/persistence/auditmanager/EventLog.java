package net.bgx.bgxnetwork.persistence.auditmanager;



import javax.persistence.CascadeType;

import javax.persistence.Column;

import javax.persistence.Entity;

import javax.persistence.GeneratedValue;

import javax.persistence.GenerationType;

import javax.persistence.Id;

import javax.persistence.JoinColumn;

import javax.persistence.ManyToOne;

import javax.persistence.Table;



@Entity

@Table(name = "EVENT_LOG")

public class EventLog implements java.io.Serializable {
    private Long id;
    private Integer code;
    private Long eventTime;
    private String login;
    private EventCode evCode;
    private String article;
    private String params;
    private Long eventDurationTime;

    @Column(name = "ARTICLE")
    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    @Column(name = "EVENT_TIME")
    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "EVENT_CODE")
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EVENT_CODE", insertable = false, updatable = false, nullable = false)
    public EventCode getEvCode() {
        return evCode;
    }

    public void setEvCode(EventCode evCode) {
        this.evCode = evCode;
    }

    @Column(name = "LOGIN")
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Column(name = "PARAM")
    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @Column(name = "DURATION")
    public Long getEventDurationTime() {
        return eventDurationTime;
    }

    public void setEventDurationTime(Long eventDurationTime) {
        this.eventDurationTime = eventDurationTime;
    }
}

