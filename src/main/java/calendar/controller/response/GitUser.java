package calendar.controller.response;

public class GitUser {
        public String login;
        public String name;
        public String email;

        public String getLogin() {
                return login;
        }

        public String getName() {
                return name;
        }

        public String getEmail() {
                return email;
        }

        @Override
        public String toString() {
                return "GitUser{" +
                        "login='" + login + '\'' +
                        ", name='" + name + '\'' +
                        ", email='" + email + '\'' +
                        '}';
        }

        //        public int id;
//        public String node_id;
//        public String avatar_url;
//        public String gravatar_id;
//        public String url;
//        public String html_url;
//        public String followers_url;
//        public String following_url;
//        public String gists_url;
//        public String starred_url;
//        public String subscriptions_url;
//        public String organizations_url;
//        public String repos_url;
//        public String events_url;
//        public String received_events_url;
//        public String type;
//        public boolean site_admin;
//        public Object company;
//        public String blog;
//        public String location;
//        public Object hireable;
//        public String bio;
//        public Object twitter_username;
//        public int public_repos;
//        public int public_gists;
//        public int followers;
//        public int following;
//        public int private_gists;
//        public int total_private_repos;
//        public int owned_private_repos;
//        public int disk_usage;
//        public int collaborators;
//        public boolean two_factor_authentication;
}
