#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>

#define PORT     8080
#define MAXLINE 1024

// Driver code
int main() {
    int sockfd;
    char buffer[MAXLINE];
    char *ok = "ok" , *bye="Goodbye";
    struct sockaddr_in servaddr, cliaddr;

    // Creating socket file descriptor
    if ( (sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0 ) {
        perror("socket creation failed");
        exit(EXIT_FAILURE) ;
    }

    memset(&servaddr, 0, sizeof(servaddr));
    memset(&cliaddr, 0, sizeof(cliaddr));

    // Filling server information
    servaddr.sin_family    = AF_INET; // IPv4
    servaddr.sin_addr.s_addr = INADDR_ANY;
    servaddr.sin_port = htons(PORT);

    // Bind the socket with the server address
    if ( bind(sockfd, (const struct sockaddr *)&servaddr,
            sizeof(servaddr)) < 0 )
    {
        perror("bind failed");
        exit(EXIT_FAILURE);
    }

    int len, n;

    while(1){
      len = sizeof(cliaddr);  //len is value/resuslt
      bzero(buffer, MAXLINE);
      n = recvfrom(sockfd, (char *)buffer, MAXLINE,
                  MSG_WAITALL, ( struct sockaddr *) &cliaddr,
                  &len);

      buffer[n] = '\0';
      printf("Client : %s\n", buffer);

      if(strncmp("Bye", buffer,3) == 0 ){
         bzero(buffer, MAXLINE);
        sendto(sockfd, (const char *)bye, strlen(bye),
            MSG_CONFIRM, (const struct sockaddr *) &cliaddr,
                len);

        printf("Server exit.\n");
            break ;
      }else{
        bzero(buffer, MAXLINE);
        sendto(sockfd, (const char *)ok, strlen(ok),
            MSG_CONFIRM, (const struct sockaddr *) &cliaddr,
                len);
          printf("ok message sent.\n");
      }

    }



    return 0;
}
