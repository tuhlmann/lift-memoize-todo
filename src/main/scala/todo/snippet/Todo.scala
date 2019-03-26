package todo.snippet

import net.liftweb.http.SHtml._
import net.liftweb.http.js.{JsCmd, _}
import net.liftweb.http.{RequestVar, SHtml}
import net.liftweb.util.Helpers._

import scala.xml.NodeSeq

case class TodoItem(id: String, title: String, completed: Boolean)
case class TodoFilters(showCompleted: Boolean = true)

object Todo {

  object TodoItems extends RequestVar[List[TodoItem]](Nil)
  object TodoListFilters extends RequestVar(TodoFilters())

  def render: NodeSeq => NodeSeq = {
    lazy val memo = SHtml.idMemoize { outer =>

      var title = ""

      def reload(): JsCmd = {
        outer.setHtml
      }

      def updateTodo(updatedItem: TodoItem): Unit = {
        TodoItems.atomicUpdate{ todos => todos.map{
          case item if item.id == updatedItem.id => updatedItem
          case item => item
        }}
      }

      def process(): JsCmd = {
        if (title.trim.nonEmpty) {
          val id = nextFuncName
          TodoItems.atomicUpdate { todos => todos ::: List(TodoItem(id, title, false)) }
          outer.setHtml()
        } else {
          JsCmds.Noop
        }
      }

      def isVisible(filter: TodoFilters)(todo: TodoItem): Boolean = {
        filter.showCompleted || !todo.completed
      }

      val isVisibleFn = isVisible(TodoListFilters.get) _
      ";todo-item" #> TodoItems.get.filter(isVisibleFn).map { todo =>
        ";title *" #> todo.title &
          (if (todo.completed) {
            ";title [class+]" #> "todo-item-done" &
            ";toggle-done" #> SHtml.ajaxCheckbox(todo.completed, v => {
              updateTodo(todo.copy(completed = v))
              reload
            })
          } else {
            ";title [class+]" #> "todo-item-open" &
              ";toggle-done" #> SHtml.ajaxCheckbox(todo.completed, v => {
                updateTodo(todo.copy(completed = v))
                reload
              })
          })
      } &
        (if (TodoListFilters.get.showCompleted) {
          ";todo-toggle-f-completed *" #> "Hide Completed" &
          ";todo-toggle-f-completed [onclick]" #> SHtml.ajaxInvoke(() => {
            TodoListFilters.atomicUpdate(_.copy(showCompleted = false))
            reload
          })
        } else {
          ";todo-toggle-f-completed *" #> "Show Completed" &
          ";todo-toggle-f-completed [onclick]" #> SHtml.ajaxInvoke(() => {
            TodoListFilters.atomicUpdate(_.copy(showCompleted = true))
            reload
          })
        }) &
      "#new-todo-title" #> (ajaxText(title, s => {title = s}) ++ hidden(() => process()))

    }

    ";todo-list-all-wrapper" #> memo

  }

}