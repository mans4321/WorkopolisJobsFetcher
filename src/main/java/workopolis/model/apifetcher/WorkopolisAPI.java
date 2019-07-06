package workopolis.model.apifetcher;

import java.util.ArrayList;
import java.util.List;

import workopolis.model.job.JobDescription;
import workopolis.model.job.JobInfoExtractor;
import workopolis.model.job.JobInfoSelectors;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class WorkopolisAPI implements FetchJobsAPI {

	private JobInfoExtractor jobInfoExtractor;

	public WorkopolisAPI(){
		jobInfoExtractor = new JobInfoExtractor();
	}
	
	@Override
	public List<String> getResultPagesURL(String jobDes, String city, int numberOfPages) {
		List<String> urls = new ArrayList<>();
		int page = 0;
		while (page < numberOfPages) {
			urls.add(constructPageUrl(jobDes, city, page));
			page++;
		}
		return urls;
	}

	@Override
	public List<String> getJobsUrlsOnPage(Document page) {
		List<String> urls = new ArrayList<>();
		
		Elements links = page.select("a.JobCard-titleLink");
		for (Element e : links) {
			String jobDeskey = e.attr("href");
			jobDeskey = jobDeskey.replaceFirst("/jobsearch/viewjob/", "");
			jobDeskey = jobDeskey.replace("ak=" + jobDeskey, "");

			final String jobDesLink = "https://www.workopolis.com/jobsearch/viewjob/" + jobDeskey;
			urls.add(jobDesLink);
		}
		return urls;
	}

	@Override
	public JobDescription extractJobDescriptionOnPage(Document page) {
		JobInfoSelectors selector = new JobInfoSelectors();
		selector.setJobDes("div.viewjob-description.ViewJob-description");
		selector.setCompanyName("div.ViewJobHeader-company");
		selector.setJobTitle("div.ViewJobHeader-title");
		selector.setCity("span.ViewJobHeader-property");
		
		JobDescription jobDes = jobInfoExtractor.extract(page, selector);
		return jobDes;
	}
	
	private String constructPageUrl(String jobDes, String city, int page) {
		jobDes = jobDes.replace(" ", "+");
		return "https://www.workopolis.com/jobsearch/find-jobs?ak=" + jobDes + "&l=" + city + "&lg=en&pn=" + page;
	}
}