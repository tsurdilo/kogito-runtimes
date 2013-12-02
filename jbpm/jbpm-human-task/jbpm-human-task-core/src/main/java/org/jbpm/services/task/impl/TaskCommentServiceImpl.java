/*
 * Copyright 2012 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.services.task.impl;

import java.util.List;

import org.kie.api.task.model.Comment;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskCommentService;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.model.InternalTaskData;

/**
 *
 */
public class TaskCommentServiceImpl implements TaskCommentService {
     
    private TaskPersistenceContext persistenceContext;

	public TaskCommentServiceImpl() {
    }
	
	public TaskCommentServiceImpl(TaskPersistenceContext persistenceContext) {
		this.persistenceContext = persistenceContext;
	}
    
    public void setPersistenceContext(TaskPersistenceContext persistenceContext) {
		this.persistenceContext = persistenceContext;
	}

    public long addComment(long taskId, Comment comment) {
        Task task = persistenceContext.findTask(taskId);
        persistenceContext.persistComment(comment);
        ((InternalTaskData) task.getTaskData()).addComment(comment);
        return comment.getId();
       
    }

    public void deleteComment(long taskId, long commentId) {
        Task task = persistenceContext.findTask(taskId);
        Comment comment = persistenceContext.findComment(commentId);
        ((InternalTaskData) task.getTaskData()).removeComment(commentId);
        persistenceContext.removeComment(comment);
    }

    public List<Comment> getAllCommentsByTaskId(long taskId) {
        Task task = persistenceContext.findTask(taskId);
        return task.getTaskData().getComments();
    }

    public Comment getCommentById(long commentId) {
        return persistenceContext.findComment(commentId);
    }
    
}
