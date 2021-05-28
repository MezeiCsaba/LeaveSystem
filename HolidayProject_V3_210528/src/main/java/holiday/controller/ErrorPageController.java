package holiday.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;

@Controller
public class ErrorPageController implements ErrorController {

	private static final String ERR_PATH = "/error";

	private ErrorAttributes errorAttr;

	@Autowired
	public void setErrorAttr(ErrorAttributes errorAttr) {
		this.errorAttr = errorAttr;
	}

	@RequestMapping(ERR_PATH)
	public String error(Model model, HttpServletRequest request) {

		ServletWebRequest rAttr = new ServletWebRequest(request);
		Map<String, Object> error = this.errorAttr.getErrorAttributes(rAttr, true);

		model.addAllAttributes(error);

		// timestamp, error, message, path, status
		// model.addAtribute("timestamp", error.get("timestamp"));
		// ...

		return "error";

	}

	@Override
	public String getErrorPath() {
		return ERR_PATH;
	}

}
