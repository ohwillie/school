function [ B ] = my_medfilt2( A, k )
[ar ac dims] = size(A);
kd2 = floor(k / 2);
km1 = k - 1;
Ap = zeros(ar + k, ac + k, dims);
Ap(kd2 + 1:ar + kd2, kd2 + 1:ac + kd2, 1:dims) = A;
B = zeros(ar, ac, dims);
for d = 1:dims
  for i = 1:ar
    for j = 1:ac
      B(i, j, d) = median(median(Ap(i:i + km1, j:j + km1, d)));
    end
  end
end
end