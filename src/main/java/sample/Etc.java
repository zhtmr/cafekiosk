package sample;

import java.util.*;
import java.io.*;

public class Etc {
  public static boolean isPossible(int m, int[] work, int maxPerDay) {
    // 현재 최대 라인수로 m일 안에 모든 작업을 처리할 수 있는지 확인하는 함수
    int days = 1;
    int currentSum = 0;

    for (int lines : work) {
      // 단일 작업이 하루 최대 작업량보다 큰 경우
      if (lines > maxPerDay) {
        return false;
      }

      // 현재 일에 더 작업을 추가할 수 없는 경우
      if (currentSum + lines > maxPerDay) {
        days++;
        currentSum = lines;
      } else {
        currentSum += lines;
      }
    }

    return days <= m;
  }

  public static int minLinesPerDay(int n, int m, int[] work) {
    // 이분 탐색을 통해 가능한 최소의 하루 최대 라인 수를 찾는 함수
    int left = Arrays.stream(work).max().getAsInt();  // 하루 최소 필요 라인 수
    int right = Arrays.stream(work).sum();  // 하루 최대 가능 라인 수
    int answer = right;

    while (left <= right) {
      int mid = (left + right) / 2;  // 중간값 계산

      // mid 값으로 m일 안에 처리 가능한지 확인
      if (isPossible(m, work, mid)) {
        answer = Math.min(answer, mid);  // 현재값이 더 작다면 정답 갱신
        right = mid - 1;  // 더 작은 값 탐색
      } else {
        left = mid + 1;  // 더 큰 값 탐색
      }
    }

    return answer;
  }

  public static void main(String[] args) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    StringTokenizer st = new StringTokenizer(br.readLine());

    int N = Integer.parseInt(st.nextToken());
    int M = Integer.parseInt(st.nextToken());

    int[] work = new int[N];
    st = new StringTokenizer(br.readLine());
    for (int i = 0; i < N; i++) {
      work[i] = Integer.parseInt(st.nextToken());
    }

    System.out.println(minLinesPerDay(N, M, work));

  }

}
