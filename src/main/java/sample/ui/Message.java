/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package sample.ui;

import java.util.Calendar;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author Rob Winch
 */
public class Message {

	private Long id;

	// @NotEmpty(message = "Message is required.")
	private String text;

	// @NotEmpty(message = "Summary is required.")
	private String summary;

	private int bidid;
	private int mima;


	@Override
	public String toString() {
		return "Message [id=" + id + ", text=" + text + ", summary=" + summary
				+ ", bidid=" + bidid + ", mima=" + mima + ", bidDate="
				+ bidDate + ", created=" + created + "]";
	}

	public int getBidid() {
		return bidid;
	}

	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date bidDate;

	public Date getBidDate() {
		return bidDate;
	}

	public void setBidDate(Date bidDate) {
		this.bidDate = bidDate;
	}

	public void setBidid(int bidid) {
		this.bidid = bidid;
	}

	public int getMima() {
		return mima;
	}

	public void setMima(int mima) {
		this.mima = mima;
	}

	private Calendar created = Calendar.getInstance();

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Calendar getCreated() {
		return this.created;
	}

	public void setCreated(Calendar created) {
		this.created = created;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
}
